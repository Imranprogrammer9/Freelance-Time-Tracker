package dev.shipkaro.kit.core.di

import androidx.room.Room
import dev.shipkaro.kit.core.config.LocalRemoteAppConfig
import dev.shipkaro.kit.core.config.RemoteAppConfig
import dev.shipkaro.kit.core.data.local.KitDatabase
import dev.shipkaro.kit.core.data.settings.SettingsRepository
import dev.shipkaro.kit.feature.auth.AuthViewModel
import dev.shipkaro.kit.feature.settings.SettingsViewModel
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
    single<RemoteAppConfig> { LocalRemoteAppConfig() } // swapped in Phase 4 (Ops)

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

private val featureModule = module {
    viewModel { AuthViewModel() }
    viewModel { SettingsViewModel(get()) }
}

val appModules = listOf(coreModule, dataModule, featureModule)
