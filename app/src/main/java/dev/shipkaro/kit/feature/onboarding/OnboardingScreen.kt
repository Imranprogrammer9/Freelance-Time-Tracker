package dev.shipkaro.kit.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.data.settings.SettingsRepository
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val settings = koinInject<SettingsRepository>()
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.onboarding_title))
        Text(stringResource(R.string.onboarding_subtitle))
        Button(onClick = {
            scope.launch {
                settings.setOnboardingDone(true)
                onFinished()
            }
        }) { Text(stringResource(R.string.action_get_started)) }
    }
}
