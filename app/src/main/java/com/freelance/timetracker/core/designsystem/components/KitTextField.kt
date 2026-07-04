package com.freelance.timetracker.core.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.freelance.timetracker.R
import com.freelance.timetracker.core.designsystem.theme.KitTheme

/**
 * Standard text field with optional leading icon, helper text, and error state.
 *
 * For password input use [KitPasswordField] which adds a show/hide eye toggle.
 */
@Composable
fun KitTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    helperText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
        trailingIcon = trailingContent,
        supportingText = helperText?.let { { Text(it) } },
        isError = isError,
        enabled = enabled,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        modifier = modifier.fillMaxWidth(),
    )
}

/**
 * Password field with show/hide toggle. Visibility state is persisted across recompositions
 * via [rememberSaveable] so config-change rotation keeps the toggle position.
 */
@Composable
fun KitPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = stringResource(R.string.auth_field_password),
    modifier: Modifier = Modifier,
    helperText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    imeAction: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    val icons = KitTheme.icons
    val showDesc = stringResource(R.string.auth_password_show)
    val hideDesc = stringResource(R.string.auth_password_hide)
    KitTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        leadingIcon = icons.lock,
        helperText = helperText,
        isError = isError,
        enabled = enabled,
        keyboardOptions = imeAction,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingContent = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) icons.visibilityOff else icons.visibility,
                    contentDescription = if (visible) hideDesc else showDesc,
                )
            }
        },
    )
}
