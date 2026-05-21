package dev.shipkaro.kit.feature.demo.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.designsystem.components.KitButton
import dev.shipkaro.kit.core.designsystem.components.KitCard
import dev.shipkaro.kit.core.designsystem.state.KitEmptyState
import dev.shipkaro.kit.core.designsystem.theme.KitTheme
import dev.shipkaro.kit.feature.demo.data.HabitEntity
import dev.shipkaro.kit.feature.demo.data.isDoneToday
import org.koin.androidx.compose.koinViewModel

/**
 * Demo Habit Tracker home — the post-auth screen in the demo flow. Real Room-backed
 * CRUD; free tier capped at [HabitsViewModel.FREE_HABIT_LIMIT], paywall beyond it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    onAddHabit: () -> Unit,
    onUpgrade: () -> Unit,
    onOpenSettings: () -> Unit,
    vm: HabitsViewModel = koinViewModel(),
) {
    val habits by vm.habits.collectAsState()
    val isPremium by vm.isPremium.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.habit_list_title)) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            KitTheme.icons.settings,
                            contentDescription = stringResource(R.string.nav_settings),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (habits.isEmpty()) {
                KitEmptyState(
                    title = stringResource(R.string.habit_empty_title),
                    message = stringResource(R.string.habit_empty_message),
                    modifier = Modifier.weight(1f),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(KitTheme.spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md),
                ) {
                    items(habits, key = { it.id }) { habit ->
                        HabitCard(
                            habit = habit,
                            onToggle = { vm.toggleToday(habit) },
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(KitTheme.spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (!isPremium) {
                    Text(
                        text = stringResource(
                            R.string.habit_free_limit_hint,
                            habits.size,
                            HabitsViewModel.FREE_HABIT_LIMIT,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.size(KitTheme.spacing.sm))
                }
                KitButton(
                    text = stringResource(R.string.habit_add),
                    icon = KitTheme.icons.add,
                    onClick = { if (vm.canAddHabit()) onAddHabit() else onUpgrade() },
                )
            }
        }
    }
}

@Composable
private fun HabitCard(habit: HabitEntity, onToggle: () -> Unit) {
    val done = habit.isDoneToday()
    KitCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.md),
        ) {
            CompletionCircle(done = done, onClick = onToggle)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        KitTheme.icons.star,
                        contentDescription = null,
                        tint = KitTheme.colors.warning,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.size(KitTheme.spacing.xs))
                    Text(
                        stringResource(R.string.habit_streak, habit.streak),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun CompletionCircle(done: Boolean, onClick: () -> Unit) {
    val color = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .then(
                if (done) {
                    Modifier.background(color)
                } else {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                },
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (done) {
            Icon(
                KitTheme.icons.check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
