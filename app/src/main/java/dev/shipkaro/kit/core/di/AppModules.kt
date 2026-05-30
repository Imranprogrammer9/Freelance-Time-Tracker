package dev.shipkaro.kit.core.di

import androidx.room.Room
import dev.shipkaro.kit.BuildConfig
import dev.shipkaro.kit.core.analytics.AnalyticsManager
import dev.shipkaro.kit.core.auth.AuthRepository
import dev.shipkaro.kit.core.auth.FirebaseAuthRepository
import dev.shipkaro.kit.core.auth.GoogleSignInManager
import dev.shipkaro.kit.core.auth.StubAuthRepository
import dev.shipkaro.kit.core.auth.SupabaseAuthRepository
import dev.shipkaro.kit.core.billing.PurchaseManager
import dev.shipkaro.kit.core.config.FirebaseRemoteAppConfig
import dev.shipkaro.kit.core.config.KitConfig
import dev.shipkaro.kit.core.config.LocalRemoteAppConfig
import dev.shipkaro.kit.core.config.RemoteAppConfig
import dev.shipkaro.kit.core.config.SupabaseRemoteAppConfig
import dev.shipkaro.kit.core.data.local.KitDatabase
import dev.shipkaro.kit.core.data.settings.SettingsRepository
import dev.shipkaro.kit.core.ops.ChangelogManager
import dev.shipkaro.kit.core.ops.InAppReviewManager
import dev.shipkaro.kit.core.ops.UpdateManager
import dev.shipkaro.kit.feature.auth.AuthViewModel
import dev.shipkaro.kit.feature.settings.SettingsViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * Koin graph. One module per concern. Apps built on the kit add their own
 * module and append it to [appModules].
 */
private val coreModule = module {
    single { SettingsRepository(androidContext()) }
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
                scheme = "shipkaro"
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
    add(featureModule)
}
