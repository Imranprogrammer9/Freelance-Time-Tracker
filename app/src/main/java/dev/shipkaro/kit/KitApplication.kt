package dev.shipkaro.kit

import android.app.Application
import dev.shipkaro.kit.core.analytics.AnalyticsManager
import dev.shipkaro.kit.core.billing.PurchaseManager
import dev.shipkaro.kit.core.config.KitConfig
import dev.shipkaro.kit.core.di.appModules
import dev.shipkaro.kit.core.log.CrashlyticsTree
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

/**
 * Application entry point. Starts Koin DI + initialises RevenueCat.
 *
 * Apps built from this kit add their own modules to [appModules].
 */
class KitApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Timber — debug builds get verbose stdout; release builds mirror WARN+ to Crashlytics
        // (only when ANALYTICS_ENABLED, matching AnalyticsManager.logError's gating).
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else if (KitConfig.ANALYTICS_ENABLED) {
            Timber.plant(CrashlyticsTree())
        }

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@KitApplication)
            modules(appModules)
        }
        // RevenueCat init. No-ops when no API key is configured.
        if (KitConfig.PAYWALL_ENABLED) {
            get<PurchaseManager>().configure()
        }
        // Analytics init. No-ops when ANALYTICS_ENABLED is off / no providers configured.
        if (KitConfig.ANALYTICS_ENABLED) {
            get<AnalyticsManager>().init()
        }
    }
}
