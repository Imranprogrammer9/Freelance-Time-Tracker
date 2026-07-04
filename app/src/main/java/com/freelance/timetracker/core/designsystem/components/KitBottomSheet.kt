package com.freelance.timetracker.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.freelance.timetracker.core.designsystem.theme.KitTheme

/**
 * Wrapper over Material 3 [ModalBottomSheet] with kit padding and skipPartiallyExpanded default.
 * Content gets a [ColumnScope].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = KitTheme.spacing.lg,
                    vertical = KitTheme.spacing.md,
                ),
            content = content,
        )
    }
}
