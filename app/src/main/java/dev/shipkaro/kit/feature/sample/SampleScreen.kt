package dev.shipkaro.kit.feature.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.R

/**
 * Demo feature. Phase 6 expands this into a full end-to-end vertical
 * (data -> repo -> VM -> UI -> analytics) as the reference pattern.
 */
@Composable
fun SampleScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(R.string.sample_title))
        Button(onClick = onBack) { Text(stringResource(R.string.action_back)) }
    }
}
