package dev.shipkaro.kit.feature.demo.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shipkaro.kit.core.billing.PurchaseManager
import dev.shipkaro.kit.feature.demo.data.HabitEntity
import dev.shipkaro.kit.feature.demo.data.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Drives the demo Habit Tracker. Habits persist in the demo-owned Room DB; the free
 * tier is capped at [FREE_HABIT_LIMIT] and lifts once [PurchaseManager.isPremium].
 */
class HabitsViewModel(
    private val repo: HabitRepository,
    private val purchases: PurchaseManager,
) : ViewModel() {

    val habits: StateFlow<List<HabitEntity>> = repo.habits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val isPremium: StateFlow<Boolean> = purchases.isPremium

    init {
        viewModelScope.launch { repo.seedIfEmpty() }
    }

    /** True when the user may add another habit (under the free cap, or premium). */
    fun canAddHabit(): Boolean =
        isPremium.value || habits.value.size < FREE_HABIT_LIMIT

    fun addHabit(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { repo.addHabit(name) }
    }

    fun toggleToday(habit: HabitEntity) {
        viewModelScope.launch { repo.toggleToday(habit) }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch { repo.deleteHabit(habit) }
    }

    companion object {
        const val FREE_HABIT_LIMIT = 3
    }
}
