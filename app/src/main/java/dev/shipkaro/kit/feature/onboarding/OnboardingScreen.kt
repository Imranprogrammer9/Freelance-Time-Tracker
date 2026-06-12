package dev.shipkaro.kit.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.data.settings.SettingsRepository
import dev.shipkaro.kit.core.designsystem.components.KitButton
import dev.shipkaro.kit.core.designsystem.onboarding.KitOnboardingPage
import dev.shipkaro.kit.core.designsystem.onboarding.KitOnboardingPager
import dev.shipkaro.kit.core.designsystem.theme.KitTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Multi-page onboarding — a swipeable [KitOnboardingPager] with a Skip action and a
 * Next / Get-started CTA. Pass [pages] to supply your own content; defaults to a
 * generic 3-page intro so the screen renders without wiring.
 */
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    pages: List<KitOnboardingPage>? = null,
) {
    val settings = koinInject<SettingsRepository>()
    val scope = rememberCoroutineScope()
    val icons = KitTheme.icons

    val resolvedPages = pages ?: listOf(
        KitOnboardingPage(
            title = stringResource(R.string.onboarding_page1_title),
            description = stringResource(R.string.onboarding_page1_desc),
            icon = icons.success,
        ),
        KitOnboardingPage(
            title = stringResource(R.string.onboarding_page2_title),
            description = stringResource(R.string.onboarding_page2_desc),
            icon = icons.add,
        ),
        KitOnboardingPage(
            title = stringResource(R.string.onboarding_page3_title),
            description = stringResource(R.string.onboarding_page3_desc),
            icon = icons.edit,
        ),
    )

    val pagerState = rememberPagerState { resolvedPages.size }
    val lastPage = pagerState.currentPage == resolvedPages.lastIndex

    val finish: () -> Unit = {
        scope.launch {
            settings.setOnboardingDone(true)
            onFinished()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KitTheme.spacing.sm, vertical = KitTheme.spacing.xs),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = finish) {
                Text(stringResource(R.string.onboarding_skip))
            }
        }

        KitOnboardingPager(
            pages = resolvedPages,
            pagerState = pagerState,
            modifier = Modifier.weight(1f),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KitTheme.spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            KitButton(
                text = stringResource(
                    if (lastPage) R.string.action_get_started else R.string.onboarding_next,
                ),
                onClick = {
                    if (lastPage) {
                        finish()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
            )
        }
    }
}
