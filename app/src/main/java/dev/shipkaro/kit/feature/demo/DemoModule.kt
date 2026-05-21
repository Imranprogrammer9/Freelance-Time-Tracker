package dev.shipkaro.kit.feature.demo

import androidx.room.Room
import dev.shipkaro.kit.feature.demo.data.DemoDatabase
import dev.shipkaro.kit.feature.demo.data.HabitRepository
import dev.shipkaro.kit.feature.demo.habits.HabitsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin graph for the demo subtree — demo-owned Room DB + habit repo + ViewModel.
 *
 * Appended to `appModules` in AppModules.kt behind a single guarded line. To remove
 * the demo entirely: delete that line, then delete this `feature/demo/` folder.
 */
val demoModule: Module = module {
    single {
        Room.databaseBuilder(androidContext(), DemoDatabase::class.java, DemoDatabase.NAME)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<DemoDatabase>().habitDao() }
    single { HabitRepository(get()) }
    viewModel { HabitsViewModel(get(), get()) }
}
