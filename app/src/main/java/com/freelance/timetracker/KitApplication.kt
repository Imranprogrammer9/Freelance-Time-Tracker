package com.freelance.timetracker

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.freelance.timetracker.core.analytics.AnalyticsManager
import com.freelance.timetracker.core.billing.PurchaseManager
import com.freelance.timetracker.core.config.KitConfig
import com.freelance.timetracker.core.di.appModules
import com.freelance.timetracker.core.log.CrashlyticsTree
import com.freelance.timetracker.core.log.SentryTree
import io.sentry.android.core.SentryAndroid
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

        // Timber — debug builds get verbose stdout; release builds mirror WARN+ to whichever
        // crash reporter(s) are wired. Crashlytics + Sentry can run in parallel — Timber
        // supports any number of trees.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            if (KitConfig.ANALYTICS_ENABLED) {
                Timber.plant(CrashlyticsTree())
            }
            if (KitConfig.SENTRY_ENABLED && BuildConfig.SENTRY_DSN.isNotBlank()) {
                SentryAndroid.init(this) { options ->
                    options.dsn = BuildConfig.SENTRY_DSN
                    options.isDebug = false
                }
                Timber.plant(SentryTree())
            }
        }

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@KitApplication)
            modules(appModules)
        }
        // RevenueCat init. No-ops when no API key is configured.
        if (KitConfig.PAYWALL_ENABLED) {
            val purchaseManager = get<PurchaseManager>()
            purchaseManager.configure()
            // Re-pull entitlements every time the app comes to the foreground. Catches
            // entitlements granted out-of-band — e.g. a Play pre-registration reward claimed
            // in the Play Store, or a purchase made on another device — so the paywall unlocks
            // without waiting for the next cold start. refresh() no-ops without an API key.
            ProcessLifecycleOwner.get().lifecycle.addObserver(
                object : DefaultLifecycleObserver {
                    override fun onStart(owner: LifecycleOwner) {
                        purchaseManager.refresh()
                    }
                },
            )
        }
        // Analytics init. No-ops when ANALYTICS_ENABLED is off / no providers configured.
        if (KitConfig.ANALYTICS_ENABLED) {
            get<AnalyticsManager>().init()
        }
    }
}
