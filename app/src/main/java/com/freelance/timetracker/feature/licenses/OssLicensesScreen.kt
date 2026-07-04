package com.freelance.timetracker.feature.licenses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.m3.rememberLibraries
import com.freelance.timetracker.R
import com.freelance.timetracker.core.designsystem.theme.KitTheme

/**
 * Settings → About → Open source licenses.
 *
 * AboutLibraries' Gradle plugin generates the full dep list at build time into the
 * `R.raw.aboutlibraries` resource. We parse it with AboutLibraries
 * (`rememberLibraries`) but render the list **ourselves** — a plain [LazyColumn]
 * plus a license dialog.
 *
 * Why not its prebuilt `LibrariesContainer`: that composable calls Compose
 * Foundation's `FlowRow`, whose binary signature changed (the `FlowRowOverflow`
 * param) between Compose versions. `aboutlibraries-compose-m3:11.6.2` was built
 * against an older signature than Compose BOM 2025.06.00 (Foundation 1.8.x) ships,
 * so it threw `NoSuchMethodError` the instant this screen opened — compiles fine,
 * crashes at runtime. Parsing is stable across versions; only its UI was the
 * problem, so we keep the parser and own the rendering. (Alternative: bump
 * AboutLibraries lib + Gradle plugin in lockstep to a Compose-1.8 build — but that
 * re-chases the version matrix on every BOM bump.)
 *
 * Note: in 11.6.2 `rememberLibraries` has no `@RawRes Int` overload — only
 * `suspend () -> String` — so we read the raw resource ourselves.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OssLicensesScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val libraries by rememberLibraries {
        context.resources.openRawResource(R.raw.aboutlibraries)
            .bufferedReader()
            .use { it.readText() }
    }
    var selected by remember { mutableStateOf<Library?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_oss_licenses)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = KitTheme.icons.back,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            items(libraries?.libraries.orEmpty()) { lib ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected = lib }
                        .padding(
                            horizontal = KitTheme.spacing.lg,
                            vertical = KitTheme.spacing.md,
                        ),
                ) {
                    Text(lib.name, style = MaterialTheme.typography.bodyLarge)
                    val subtitle = listOfNotNull(
                        lib.artifactVersion,
                        lib.developers.firstOrNull()?.name,
                        lib.licenses.firstOrNull()?.name,
                    ).joinToString(" · ")
                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                HorizontalDivider()
            }
        }
    }

    selected?.let { lib ->
        val license = lib.licenses.firstOrNull()
        val body = license?.licenseContent?.takeIf { it.isNotBlank() }
            ?: license?.url
            ?: stringResource(R.string.oss_license_unavailable)
        AlertDialog(
            onDismissRequest = { selected = null },
            confirmButton = {
                TextButton(onClick = { selected = null }) {
                    Text(stringResource(R.string.oss_license_close))
                }
            },
            title = {
                Text(lib.artifactVersion?.let { "${lib.name} $it" } ?: lib.name)
            },
            text = {
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState()),
                )
            },
        )
    }
}
