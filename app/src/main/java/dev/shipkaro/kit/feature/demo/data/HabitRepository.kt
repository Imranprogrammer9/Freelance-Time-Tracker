package dev.shipkaro.kit.feature.demo.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Habit data for the demo. Wraps [HabitDao], seeds a few sample habits on first run
 * so the list is never empty, and owns the "done today" + streak logic.
 */
class HabitRepository(private val dao: HabitDao) {

    val habits: Flow<List<HabitEntity>> = dao.observeAll()

    /** Insert sample habits the first time the demo is opened. */
    suspend fun seedIfEmpty() {
        if (dao.count() > 0) return
        listOf("Drink water", "Read 10 pages", "Morning walk").forEach {
            dao.insert(HabitEntity(name = it))
        }
    }

    suspend fun addHabit(name: String) {
        dao.insert(HabitEntity(name = name.trim()))
    }

    suspend fun deleteHabit(habit: HabitEntity) {
        dao.delete(habit)
    }

    /** Toggle today's completion: marks done (streak++) or undoes it (streak--). */
    suspend fun toggleToday(habit: HabitEntity) {
        val today = LocalDate.now().toEpochDay()
        val updated = if (habit.lastCompletedEpochDay == today) {
            habit.copy(
                streak = (habit.streak - 1).coerceAtLeast(0),
                lastCompletedEpochDay = null,
            )
        } else {
            habit.copy(
                streak = habit.streak + 1,
                lastCompletedEpochDay = today,
            )
        }
        dao.update(updated)
    }
}

/** True if [HabitEntity] was completed on the current day. */
fun HabitEntity.isDoneToday(): Boolean =
    lastCompletedEpochDay == LocalDate.now().toEpochDay()
