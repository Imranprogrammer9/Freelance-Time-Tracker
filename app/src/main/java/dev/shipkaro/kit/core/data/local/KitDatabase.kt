package dev.shipkaro.kit.core.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

/**
 * Offline store. One sample entity shows the Room pattern end-to-end;
 * apps add their own @Entity / @Dao and bump the version.
 */
@Entity(tableName = "sample_items")
data class SampleItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
)

@Dao
interface SampleDao {
    @Query("SELECT * FROM sample_items ORDER BY id DESC")
    fun observeAll(): Flow<List<SampleItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SampleItem)
}

@Database(entities = [SampleItem::class], version = 1, exportSchema = false)
abstract class KitDatabase : RoomDatabase() {
    abstract fun sampleDao(): SampleDao

    companion object {
        const val NAME = "shipkaro_kit.db"
    }
}
