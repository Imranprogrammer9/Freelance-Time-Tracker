package dev.shipkaro.kit.feature.licenses

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.designsystem.theme.KitTheme

/**
 * Settings → About → Open source licenses.
 *
 * Renders the full Gradle dep tree via mikepenz/AboutLibraries
 * (`LibrariesContainer` from `aboutlibraries-compose-m3`). The list itself is
 * generated at build time by the AboutLibraries Gradle plugin and ships as the
 * `aboutlibraries.json` asset — no runtime fetching, no Internet permission
 * required just for this screen.
 *
 * Replaced the earlier `OssLicensesMenuActivity` integration which only listed
 * Google Play Services / Firebase artifacts (the gms plugin relies on a
 * `third_party_licenses` resource that non-Google libs don't publish, so the
 * list came up blank for most kit deps).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OssLicensesScreen(onBack: () -> Unit) {
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
        LibrariesContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        )
    }
}
