package com.freelance.timetracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.freelance.timetracker.core.config.KitConfig
import com.freelance.timetracker.core.data.settings.SettingsRepository
import com.freelance.timetracker.core.designsystem.theme.ShipKaroTheme
import com.freelance.timetracker.core.navigation.KitNavHost
import com.freelance.timetracker.core.ops.UpdateGate
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import org.koin.android.ext.android.inject
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity() {
    private val settings: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        forwardDeeplinkToSupabase(intent)
        setContent {
            val themeMode by settings.themeMode.collectAsState(initial = null)
            ShipKaroTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    UpdateGate {
                        KitNavHost()
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        forwardDeeplinkToSupabase(intent)
    }

    /**
     * Routes OAuth callbacks (e.g. Supabase redirect-to scheme) into the Supabase client
     * so the auth plugin can finalise the session. No-op when AUTH_PROVIDER != SUPABASE.
     */
    private fun forwardDeeplinkToSupabase(intent: Intent) {
        if (KitConfig.AUTH_PROVIDER != KitConfig.AuthProvider.SUPABASE) return
        val client = runCatching {
            GlobalContext.get().get<SupabaseClient>()
        }.getOrNull() ?: return
        client.handleDeeplinks(intent)
    }
}
