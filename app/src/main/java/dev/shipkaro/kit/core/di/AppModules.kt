package dev.shipkaro.kit.core.di

import androidx.room.Room
import dev.shipkaro.kit.BuildConfig
import dev.shipkaro.kit.core.auth.AuthRepository
import dev.shipkaro.kit.core.auth.FirebaseAuthRepository
import dev.shipkaro.kit.core.auth.GoogleSignInManager
import dev.shipkaro.kit.core.analytics.AnalyticsManager
import dev.shipkaro.kit.core.auth.StubAuthRepository
import dev.shipkaro.kit.core.auth.SupabaseAuthRepository
import dev.shipkaro.kit.core.billing.PurchaseManager
import dev.shipkaro.kit.core.config.FirebaseRemoteAppConfig
import dev.shipkaro.kit.core.config.KitConfig
import dev.shipkaro.kit.core.config.LocalRemoteAppConfig
import dev.shipkaro.kit.core.config.RemoteAppConfig
import dev.shipkaro.kit.core.data.local.KitDatabase
import dev.shipkaro.kit.core.data.settings.SettingsRepository
import dev.shipkaro.kit.core.ops.InAppReviewManager
import dev.shipkaro.kit.core.ops.UpdateManager
import dev.shipkaro.kit.feature.auth.AuthViewModel
import dev.shipkaro.kit.feature.paywall.PurchaseViewModel
import dev.shipkaro.kit.feature.settings.SettingsViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
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
        // baseUrl is a placeholder; the app sets its real API host here.
        Retrofit.Builder()
            .baseUrl("https://example.com/")
            .client(get())
            .build()
    }
}

private val dataModule = module {
    single {
        Room.databaseBuilder(androidContext(), KitDatabase::class.java, KitDatabase.NAME)
            .fallbackToDestructiveMigration()
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
private val authModule = module {
    when (KitConfig.AUTH_PROVIDER) {
        KitConfig.AuthProvider.SUPABASE -> {
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
                    // ktor engine auto-resolved to OkHttp via ktor-client-okhttp on classpath.
                }
            }
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

/** Billing graph. PurchaseManager is a singleton; no-ops when no RevenueCat key is set. */
private val billingModule = module {
    single { PurchaseManager(androidContext()) }
}

/** Analytics graph. AnalyticsManager is a singleton; backends degrade gracefully. */
private val analyticsModule = module {
    single { AnalyticsManager(androidContext(), get()) }
}

/** Ops graph — update gate + in-app review. */
private val opsModule = module {
    single { UpdateManager(get()) }
    single { InAppReviewManager() }
}

private val featureModule = module {
    viewModel { AuthViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
    viewModel { PurchaseViewModel(get()) }
}

val appModules = listOf(
    coreModule, dataModule, authModule, billingModule, analyticsModule, opsModule, featureModule,
)
