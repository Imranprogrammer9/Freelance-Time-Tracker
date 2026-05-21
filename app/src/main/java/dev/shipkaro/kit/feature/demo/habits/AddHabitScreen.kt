package dev.shipkaro.kit.feature.demo.habits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.designsystem.components.KitButton
import dev.shipkaro.kit.core.designsystem.components.KitTextField
import dev.shipkaro.kit.core.designsystem.theme.KitTheme
import org.koin.androidx.compose.koinViewModel

/** Demo — add a new habit. Saves to the demo Room DB and pops back to the list. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    onDone: () -> Unit,
    vm: HabitsViewModel = koinViewModel(),
) {
    var name by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.habit_add_title)) },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(KitTheme.icons.back, contentDescription = stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(KitTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.lg),
        ) {
            KitTextField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(R.string.habit_name_label),
                leadingIcon = KitTheme.icons.edit,
            )
            KitButton(
                text = stringResource(R.string.habit_save),
                onClick = {
                    vm.addHabit(name)
                    onDone()
                },
                enabled = name.isNotBlank(),
            )
        }
    }
}
