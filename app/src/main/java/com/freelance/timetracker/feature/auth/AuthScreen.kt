package com.freelance.timetracker.feature.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.freelance.timetracker.R
import com.freelance.timetracker.core.analytics.AnalyticsManager
import com.freelance.timetracker.core.analytics.ScreenNames
import com.freelance.timetracker.core.auth.messageRes
import com.freelance.timetracker.core.config.KitConfig
import com.freelance.timetracker.core.designsystem.components.KitBanner
import com.freelance.timetracker.core.designsystem.components.KitBannerStyle
import com.freelance.timetracker.core.designsystem.components.KitButton
import com.freelance.timetracker.core.designsystem.components.KitButtonStyle
import com.freelance.timetracker.core.designsystem.components.KitPasswordField
import com.freelance.timetracker.core.designsystem.components.KitTextField
import com.freelance.timetracker.core.designsystem.theme.KitTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * Single auth screen, mode-switching between Sign In / Sign Up / Forgot Password.
 * Backed by [AuthViewModel] + [com.freelance.timetracker.core.auth.AuthRepository].
 */
@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    vm: AuthViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsState()
    val activityContext = LocalContext.current
    val analytics = koinInject<AnalyticsManager>()

    LaunchedEffect(Unit) { analytics.logScreen(ScreenNames.AUTH) }
    LaunchedEffect(state.status) {
        if (state.status is AuthViewModel.Status.Authenticated) onAuthenticated()
    }

    val loading = state.status is AuthViewModel.Status.Loading
    val emailEnabled = KitConfig.EMAIL_SIGN_IN_ENABLED
    val googleEnabled = KitConfig.GOOGLE_SIGN_IN_ENABLED

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(KitTheme.spacing.lg)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(KitTheme.spacing.xxl))

        Image(
            painter = painterResource(R.drawable.ic_shipkaro_mark),
            contentDescription = null,
            modifier = Modifier.size(56.dp),
        )

        Text(
            text = stringResource(
                when (state.mode) {
                    AuthViewModel.Mode.SIGN_IN -> R.string.auth_title_sign_in
                    AuthViewModel.Mode.SIGN_UP -> R.string.auth_title_sign_up
                    AuthViewModel.Mode.FORGOT -> R.string.auth_title_forgot
                },
            ),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(
                when (state.mode) {
                    AuthViewModel.Mode.SIGN_IN -> R.string.auth_subtitle_sign_in
                    AuthViewModel.Mode.SIGN_UP -> R.string.auth_subtitle_sign_up
                    AuthViewModel.Mode.FORGOT -> R.string.auth_subtitle_forgot
                },
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(KitTheme.spacing.sm))

        StatusBanner(state.status)

        if (emailEnabled) {
            KitTextField(
                value = state.email,
                onValueChange = vm::setEmail,
                label = stringResource(R.string.auth_field_email),
                leadingIcon = KitTheme.icons.email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )

            if (state.mode != AuthViewModel.Mode.FORGOT) {
                KitPasswordField(
                    value = state.password,
                    onValueChange = vm::setPassword,
                    label = stringResource(R.string.auth_field_password),
                )
            }

            if (state.mode == AuthViewModel.Mode.SIGN_IN) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { vm.setMode(AuthViewModel.Mode.FORGOT) }) {
                        Text(stringResource(R.string.auth_link_forgot_password))
                    }
                }
            }

            val primaryLabel = stringResource(
                when (state.mode) {
                    AuthViewModel.Mode.SIGN_IN -> R.string.auth_action_sign_in
                    AuthViewModel.Mode.SIGN_UP -> R.string.auth_action_sign_up
                    AuthViewModel.Mode.FORGOT -> R.string.auth_action_send_reset
                },
            )
            KitButton(
                text = primaryLabel,
                onClick = {
                    when (state.mode) {
                        AuthViewModel.Mode.SIGN_IN -> vm.signIn()
                        AuthViewModel.Mode.SIGN_UP -> vm.signUp()
                        AuthViewModel.Mode.FORGOT -> vm.sendReset()
                    }
                },
                loading = loading,
                enabled = state.email.isNotBlank() &&
                    (state.mode == AuthViewModel.Mode.FORGOT || state.password.isNotBlank()),
            )
        }

        val showGoogle = googleEnabled && state.mode != AuthViewModel.Mode.FORGOT
        if (emailEnabled && showGoogle) {
            OrDivider()
        }
        if (showGoogle) {
            KitButton(
                text = stringResource(R.string.auth_action_continue_with_google),
                onClick = { vm.signInWithGoogle(activityContext) },
                style = KitButtonStyle.SECONDARY,
                icon = KitTheme.icons.google,
                loading = loading,
            )
        }

        Spacer(Modifier.height(KitTheme.spacing.xs))

        if (emailEnabled) {
            ModeSwitchLink(
                text = stringResource(
                    when (state.mode) {
                        AuthViewModel.Mode.SIGN_IN -> R.string.auth_link_create_account
                        AuthViewModel.Mode.SIGN_UP -> R.string.auth_link_have_account
                        AuthViewModel.Mode.FORGOT -> R.string.auth_link_back_to_sign_in
                    },
                ),
                onClick = {
                    vm.setMode(
                        if (state.mode == AuthViewModel.Mode.SIGN_UP) {
                            AuthViewModel.Mode.SIGN_IN
                        } else if (state.mode == AuthViewModel.Mode.FORGOT) {
                            AuthViewModel.Mode.SIGN_IN
                        } else {
                            AuthViewModel.Mode.SIGN_UP
                        },
                    )
                },
            )
        }
    }
}

@Composable
private fun OrDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = KitTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.md),
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
        Text(
            stringResource(R.string.auth_or),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun ModeSwitchLink(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun StatusBanner(status: AuthViewModel.Status) {
    when (status) {
        is AuthViewModel.Status.Error -> KitBanner(
            text = stringResource(status.code.messageRes()),
            style = KitBannerStyle.ERROR,
        )
        AuthViewModel.Status.EmailConfirmationRequired -> KitBanner(
            text = stringResource(R.string.auth_banner_email_confirmation),
            style = KitBannerStyle.INFO,
        )
        AuthViewModel.Status.ResetEmailSent -> KitBanner(
            text = stringResource(R.string.auth_banner_reset_sent),
            style = KitBannerStyle.SUCCESS,
        )
        else -> Unit
    }
}
