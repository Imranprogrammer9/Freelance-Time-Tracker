package dev.shipkaro.kit

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
import dev.shipkaro.kit.core.data.settings.SettingsRepository
import dev.shipkaro.kit.core.designsystem.theme.ShipKaroTheme
import dev.shipkaro.kit.core.navigation.KitNavHost
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val settings: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val themeMode by settings.themeMode.collectAsState(initial = null)
            ShipKaroTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    KitNavHost()
                }
            }
        }
    }
}
