package dev.shipkaro.kit.feature.demo.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Demo-owned Room store for the bundled Habit Tracker sample.
 *
 * Deliberately SEPARATE from the kit's `KitDatabase` so the whole demo stays
 * self-contained: deleting `feature/demo/` removes this database definition with it,
 * leaving the kit clean. Real persistence — habits survive restarts — just device-local.
 */
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val streak: Int = 0,
    /** epoch-day of the last completion, or null if never completed. */
    val lastCompletedEpochDay: Long? = null,
)

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY id ASC")
    fun observeAll(): Flow<List<HabitEntity>>

    @Query("SELECT COUNT(*) FROM habits")
    suspend fun count(): Int

    @Insert
    suspend fun insert(habit: HabitEntity)

    @Update
    suspend fun update(habit: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)
}

@Database(entities = [HabitEntity::class], version = 1, exportSchema = false)
abstract class DemoDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    companion object {
        const val NAME = "shipkaro_demo.db"
    }
}
