package dev.shipkaro.kit

import android.app.Application
import dev.shipkaro.kit.core.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application entry point. Starts Koin DI.
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
    }
}
