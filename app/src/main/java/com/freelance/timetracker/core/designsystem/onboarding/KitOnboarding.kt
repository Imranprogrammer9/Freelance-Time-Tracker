package com.freelance.timetracker.core.designsystem.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.freelance.timetracker.core.designsystem.theme.KitTheme

/** Data class describing one onboarding page. Pass a list to [KitOnboardingPager]. */
data class KitOnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector? = null,
)

/** Dot indicator row, [pageCount] dots, [currentPage] highlighted. */
@Composable
fun KitPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { i ->
            val active = i == currentPage
            val color = if (active) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
            Box(
                modifier = Modifier
                    .size(if (active) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

/**
 * Out-of-the-box onboarding pager: icon + title + description per page, dot indicator below.
 * For custom page layouts, drop down to [HorizontalPager] directly and reuse [KitPageIndicator].
 */
@Composable
fun KitOnboardingPager(
    pages: List<KitOnboardingPage>,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState { pages.size },
) {
    Column(modifier = modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { i ->
            val page = pages[i]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(KitTheme.spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (page.icon != null) {
                    Icon(
                        page.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(96.dp),
                    )
                    Spacer(Modifier.height(KitTheme.spacing.xl))
                }
                Text(
                    page.title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(KitTheme.spacing.md))
                Text(
                    page.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = KitTheme.spacing.lg),
            contentAlignment = Alignment.Center,
        ) {
            KitPageIndicator(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
            )
        }
    }
}
