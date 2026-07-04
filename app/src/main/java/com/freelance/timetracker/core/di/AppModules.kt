package com.freelance.timetracker.core.di

import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.freelance.timetracker.BuildConfig
import com.freelance.timetracker.core.ai.OpenRouterAiClient
import com.freelance.timetracker.core.ai.OpenRouterAiRepository
import com.freelance.timetracker.core.ai.OpenRouterAuthInterceptor
import com.freelance.timetracker.core.analytics.AnalyticsManager
import com.freelance.timetracker.core.auth.AuthRepository
import com.freelance.timetracker.core.auth.FirebaseAuthRepository
import com.freelance.timetracker.core.auth.GoogleSignInManager
import com.freelance.timetracker.core.auth.StubAuthRepository
import com.freelance.timetracker.core.auth.SupabaseAuthRepository
import com.freelance.timetracker.core.billing.PurchaseManager
import com.freelance.timetracker.core.config.FirebaseRemoteAppConfig
import com.freelance.timetracker.core.config.KitConfig
import com.freelance.timetracker.core.config.LocalRemoteAppConfig
import com.freelance.timetracker.core.config.RemoteAppConfig
import com.freelance.timetracker.core.config.SupabaseRemoteAppConfig
import com.freelance.timetracker.core.data.local.KitDatabase
import com.freelance.timetracker.core.data.settings.SettingsRepository
import com.freelance.timetracker.core.ops.ChangelogManager
import com.freelance.timetracker.core.ops.InAppReviewManager
import com.freelance.timetracker.core.ops.UpdateManager
import com.freelance.timetracker.core.security.SecureDataStore
import com.freelance.timetracker.feature.auth.AuthViewModel
import com.freelance.timetracker.feature.settings.SettingsViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * Koin graph. One module per concern. Apps built on the kit add their own
 * module and append it to [appModules].
 */
private val coreModule = module {
    single { SettingsRepository(androidContext()) }
    single { SecureDataStore(androidContext()) }
    single<RemoteAppConfig> {
        when (KitConfig.REMOTE_CONFIG_PROVIDER) {
            KitConfig.RemoteConfigProvider.FIREBASE -> FirebaseRemoteAppConfig()
            KitConfig.RemoteConfigProvider.SUPABASE -> SupabaseRemoteAppConfig(get())
            KitConfig.RemoteConfigProvider.LOCAL -> LocalRemoteAppConfig()
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                },
            )
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl(KitConfig.API_BASE_URL)
            .client(get())
            .build()
    }
}

private val dataModule = module {
    single {
        Room.databaseBuilder(androidContext(), KitDatabase::class.java, KitDatabase.NAME)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<KitDatabase>().sampleDao() }
}

/**
 * Auth graph. Picks impl by [KitConfig.AUTH_PROVIDER] at graph-init time.
 *
 * STUB     -> in-memory, no creds, no SupabaseClient created
 * SUPABASE -> creates a SupabaseClient from BuildConfig URL/key, installs Auth plugin
 * FIREBASE -> uses FirebaseAuth.getInstance() — requires google-services.json
 */
/**
 * Supabase client graph. Created once and reused for any provider that needs
 * Postgres / Auth on Supabase — currently Supabase auth and/or the Supabase
 * `RemoteAppConfig`. Only added to [appModules] when at least one of those uses
 * Supabase (see [needsSupabaseClient]).
 */
private val supabaseClientModule = module {
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY,
        ) {
            install(Auth) {
                // OAuth callback scheme/host — must match AndroidManifest.xml intent-filter
                // AND your Supabase project's Redirect URL setting.
                scheme = "timetracker"
                host = "auth-callback"
            }
            install(Postgrest)
            // ktor engine auto-resolved to OkHttp via ktor-client-okhttp on classpath.
        }
    }
}

private val authModule = module {
    when (KitConfig.AUTH_PROVIDER) {
        KitConfig.AuthProvider.SUPABASE -> {
            single<AuthRepository> { SupabaseAuthRepository(get()) }
        }
        KitConfig.AuthProvider.FIREBASE -> {
            single<AuthRepository> { FirebaseAuthRepository() }
        }
        KitConfig.AuthProvider.STUB -> {
            single<AuthRepository> { StubAuthRepository() }
        }
    }
    single { GoogleSignInManager(androidContext()) }
}

private fun needsSupabaseClient(): Boolean =
    KitConfig.AUTH_PROVIDER == KitConfig.AuthProvider.SUPABASE ||
        KitConfig.REMOTE_CONFIG_PROVIDER == KitConfig.RemoteConfigProvider.SUPABASE

/** Billing graph. PurchaseManager is a singleton; no-ops when no RevenueCat key is set. */
private val billingModule = module {
    single { PurchaseManager(androidContext()) }
}

/** Analytics graph. AnalyticsManager is a singleton; backends degrade gracefully. */
private val analyticsModule = module {
    single { AnalyticsManager(androidContext(), get()) }
}

/** Ops graph — update gate, changelog, in-app review. */
private val opsModule = module {
    single { UpdateManager(get()) }
    single { ChangelogManager(get()) }
    single { InAppReviewManager() }
}

/**
 * AI graph — OpenRouter chat / streaming / structured-JSON. Only added to [appModules]
 * when [KitConfig.OPENROUTER_ENABLED] is true so the unused Retrofit / OkHttp graph
 * isn't built for apps without AI features.
 *
 * The OpenRouter Retrofit is a **separate** named instance — its base URL, auth
 * header, and converter never touch the app's own [KitConfig.API_BASE_URL] Retrofit.
 */
private val aiModule = module {
    single(named("ai_json")) {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
    single(named("retrofit_openrouter")) {
        val json = get<Json>(named("ai_json"))
        Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/v1/")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        OpenRouterAuthInterceptor(
                            apiKey = BuildConfig.OPENROUTER_API_KEY,
                            appName = "NowKit",
                        ),
                    )
                    .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BASIC
                        },
                    )
                    .build(),
            )
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    single { get<Retrofit>(named("retrofit_openrouter")).create(OpenRouterAiClient::class.java) }
    single { OpenRouterAiRepository(get(), get(named("ai_json"))) }
}

private val featureModule = module {
    viewModel { AuthViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
}

val appModules = buildList {
    add(coreModule)
    add(dataModule)
    if (needsSupabaseClient()) add(supabaseClientModule)
    add(authModule)
    add(billingModule)
    add(analyticsModule)
    add(opsModule)
    if (KitConfig.OPENROUTER_ENABLED) add(aiModule)
    add(featureModule)
}
