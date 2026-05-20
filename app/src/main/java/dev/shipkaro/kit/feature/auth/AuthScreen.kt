package dev.shipkaro.kit.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.auth.messageRes
import dev.shipkaro.kit.core.designsystem.components.KitBanner
import dev.shipkaro.kit.core.designsystem.components.KitBannerStyle
import dev.shipkaro.kit.core.designsystem.components.KitButton
import dev.shipkaro.kit.core.designsystem.components.KitButtonStyle
import dev.shipkaro.kit.core.designsystem.components.KitPasswordField
import dev.shipkaro.kit.core.designsystem.components.KitTextField
import dev.shipkaro.kit.core.designsystem.theme.KitTheme
import org.koin.androidx.compose.koinViewModel

/**
 * Single auth screen with mode-switching tabs (SignIn / SignUp / ForgotPassword).
 * Backed by [AuthViewModel] + [dev.shipkaro.kit.core.auth.AuthRepository].
 */
@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    vm: AuthViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsState()
    val activityContext = LocalContext.current

    LaunchedEffect(state.status) {
        if (state.status is AuthViewModel.Status.Authenticated) onAuthenticated()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(KitTheme.spacing.lg)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md),
        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(Modifier.height(KitTheme.spacing.xxl))

        Text(
            text = stringResource(
                when (state.mode) {
                    AuthViewModel.Mode.SIGN_IN -> R.string.auth_title_sign_in
                    AuthViewModel.Mode.SIGN_UP -> R.string.auth_title_sign_up
                    AuthViewModel.Mode.FORGOT -> R.string.auth_title_forgot
                },
            ),
            style = MaterialTheme.typography.headlineMedium,
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
        )

        Spacer(Modifier.height(KitTheme.spacing.md))

        StatusBanner(state.status)

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

        val loading = state.status is AuthViewModel.Status.Loading
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

        if (state.mode != AuthViewModel.Mode.FORGOT) {
            Spacer(Modifier.height(KitTheme.spacing.sm))
            KitButton(
                text = stringResource(R.string.auth_action_continue_with_google),
                onClick = { vm.signInWithGoogle(activityContext) },
                style = KitButtonStyle.SECONDARY,
                icon = KitTheme.icons.google,
                loading = loading,
            )
        }

        Spacer(Modifier.height(KitTheme.spacing.sm))

        // Secondary actions (mode switch).
        when (state.mode) {
            AuthViewModel.Mode.SIGN_IN -> {
                KitButton(
                    text = stringResource(R.string.auth_link_forgot_password),
                    onClick = { vm.setMode(AuthViewModel.Mode.FORGOT) },
                    style = KitButtonStyle.TEXT,
                )
                KitButton(
                    text = stringResource(R.string.auth_link_create_account),
                    onClick = { vm.setMode(AuthViewModel.Mode.SIGN_UP) },
                    style = KitButtonStyle.SECONDARY,
                )
            }
            AuthViewModel.Mode.SIGN_UP -> {
                KitButton(
                    text = stringResource(R.string.auth_link_have_account),
                    onClick = { vm.setMode(AuthViewModel.Mode.SIGN_IN) },
                    style = KitButtonStyle.SECONDARY,
                )
            }
            AuthViewModel.Mode.FORGOT -> {
                KitButton(
                    text = stringResource(R.string.auth_link_back_to_sign_in),
                    onClick = { vm.setMode(AuthViewModel.Mode.SIGN_IN) },
                    style = KitButtonStyle.TEXT,
                )
            }
        }
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
