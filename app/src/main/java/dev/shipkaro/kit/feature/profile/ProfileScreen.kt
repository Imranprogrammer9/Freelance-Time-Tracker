package dev.shipkaro.kit.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.designsystem.components.KitAvatar
import dev.shipkaro.kit.core.designsystem.components.KitButton
import dev.shipkaro.kit.core.designsystem.components.KitButtonStyle
import dev.shipkaro.kit.core.designsystem.theme.KitTheme
import dev.shipkaro.kit.feature.settings.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Read-only profile screen. Shows the signed-in user's name, email, and avatar (if any),
 * plus a Sign out button. Extend with editable fields if your app needs them.
 *
 * When the user signs out, the kit's nav host detects the SignedOut session state and
 * routes back to Auth automatically — this screen does not need an explicit redirect.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    vm: SettingsViewModel = koinViewModel(),
) {
    val user by vm.currentUser.collectAsState(initial = null)
    val fallback = stringResource(R.string.settings_account_default_name)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(KitTheme.icons.back, contentDescription = stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        val u = user
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(KitTheme.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md),
        ) {
            Spacer(Modifier.height(KitTheme.spacing.lg))
            KitAvatar(
                imageUrl = u?.avatarUrl,
                initials = (u?.displayName ?: u?.email ?: fallback).take(1),
            )
            Spacer(Modifier.height(KitTheme.spacing.md))
            Text(
                text = u?.displayName ?: u?.email ?: fallback,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            u?.email?.takeIf { it.isNotBlank() }?.let { email ->
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(KitTheme.spacing.xxl))

            KitButton(
                text = stringResource(R.string.profile_sign_out),
                onClick = { vm.signOut() },
                style = KitButtonStyle.SECONDARY,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
