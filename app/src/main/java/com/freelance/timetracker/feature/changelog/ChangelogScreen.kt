package com.freelance.timetracker.feature.changelog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.freelance.timetracker.BuildConfig
import com.freelance.timetracker.R
import com.freelance.timetracker.core.analytics.AnalyticsManager
import com.freelance.timetracker.core.analytics.ScreenNames
import com.freelance.timetracker.core.designsystem.ops.ChangelogBullet
import com.freelance.timetracker.core.designsystem.state.KitEmptyState
import com.freelance.timetracker.core.designsystem.theme.KitTheme
import com.freelance.timetracker.core.ops.ChangelogManager
import com.freelance.timetracker.core.ops.ChangelogRelease
import org.koin.compose.koinInject

/**
 * Version-history screen — renders the backend-published changelog ([ChangelogManager]).
 * Reachable from Settings → About → What's New.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangelogScreen(onBack: () -> Unit) {
    val changelogManager = koinInject<ChangelogManager>()
    val analytics = koinInject<AnalyticsManager>()
    val releases by changelogManager.releases.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) { analytics.logScreen(ScreenNames.CHANGELOG) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.changelog_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            KitTheme.icons.back,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        if (releases.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                KitEmptyState(
                    title = stringResource(R.string.changelog_empty_title),
                    message = stringResource(R.string.changelog_empty_message),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(KitTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md),
            ) {
                items(releases, key = { it.versionCode }) { release ->
                    ReleaseCard(release = release)
                }
            }
        }
    }
}

@Composable
private fun ReleaseCard(release: ChangelogRelease, modifier: Modifier = Modifier) {
    val installed = release.versionCode == BuildConfig.VERSION_CODE
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(KitTheme.spacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.changelog_version, release.versionName),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                if (installed) {
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(start = KitTheme.spacing.sm))
                    InstalledBadge()
                }
                androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                if (release.date.isNotEmpty()) {
                    Text(
                        text = release.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = KitTheme.spacing.sm))
            release.notes.forEach { note -> ChangelogBullet(note) }
        }
    }
}

@Composable
private fun InstalledBadge() {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Text(
            text = stringResource(R.string.changelog_installed_badge),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}
