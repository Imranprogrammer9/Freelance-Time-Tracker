package com.freelance.timetracker.core.designsystem.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.freelance.timetracker.R

/**
 * Modal date picker — wraps Material 3 [DatePickerDialog]. Caller controls
 * visibility via [visible]; dismiss + confirm both surface the selected epoch
 * millis (or null if the user cleared the date).
 *
 * For an inline (non-modal) date picker, use [KitInlineDatePicker].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitDatePicker(
    visible: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    state: DatePickerState = rememberDatePickerState(),
    confirmLabel: String = stringResource(R.string.kit_datepicker_confirm),
    dismissLabel: String = stringResource(R.string.kit_datepicker_dismiss),
) {
    if (!visible) return
    DatePickerDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(state.selectedDateMillis)
                onDismiss()
            }) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(dismissLabel) }
        },
    ) {
        DatePicker(state = state)
    }
}

/** Inline date picker — embed in a sheet / dialog / form. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitInlineDatePicker(
    state: DatePickerState = rememberDatePickerState(),
    modifier: Modifier = Modifier,
) {
    DatePicker(state = state, modifier = modifier)
}
