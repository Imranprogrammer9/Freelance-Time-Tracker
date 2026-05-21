package dev.shipkaro.kit

import android.app.Application
import dev.shipkaro.kit.core.billing.PurchaseManager
import dev.shipkaro.kit.core.config.KitConfig
import dev.shipkaro.kit.core.di.appModules
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application entry point. Starts Koin DI + initialises RevenueCat.
 *
 * Apps built from this kit add their own modules to [appModules].
 */
class KitApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@KitApplication)
            modules(appModules)
        }
        // RevenueCat init. No-ops when no API key is configured.
        if (KitConfig.PAYWALL_ENABLED) {
            get<PurchaseManager>().configure()
        }
    }
}
