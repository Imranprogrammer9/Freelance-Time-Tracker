package dev.shipkaro.kit.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.R
import kotlinx.coroutines.delay

/**
 * Branded splash. Shows for [SPLASH_MILLIS], then calls [onReady] so the host can
 * resolve the next route (Onboarding / Auth / Paywall / Home). Replace the artwork
 * by swapping the `ic_shipkaro_mark` drawable with your own brand mark.
 */
@Composable
fun SplashScreen(onReady: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(SPLASH_MILLIS)
        onReady()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_shipkaro_mark),
            contentDescription = null,
            modifier = Modifier.size(96.dp),
        )
    }
}

private const val SPLASH_MILLIS = 1500L
