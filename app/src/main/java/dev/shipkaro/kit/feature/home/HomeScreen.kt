package dev.shipkaro.kit.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.config.KitConfig

@Composable
fun HomeScreen(onOpenSettings: () -> Unit, onOpenSample: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.home_title))
        Button(onClick = onOpenSettings) { Text(stringResource(R.string.nav_settings)) }
        if (KitConfig.SAMPLE_FEATURE_ENABLED) {
            Button(onClick = onOpenSample) { Text(stringResource(R.string.nav_sample)) }
        }
    }
}
