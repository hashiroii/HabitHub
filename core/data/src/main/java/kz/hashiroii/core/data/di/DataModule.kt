package kz.hashiroii.core.data.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kz.hashiroii.core.data.database.HabitHubDatabase
import kz.hashiroii.core.data.database.dao.HabitCompletionDao
import kz.hashiroii.core.data.database.dao.HabitDao
import kz.hashiroii.core.data.repository.HabitRepositoryImpl
import kz.hashiroii.core.domain.repository.HabitRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): HabitHubDatabase =
            Room.databaseBuilder(
                context,
                HabitHubDatabase::class.java,
                "habithub.db",
            ).fallbackToDestructiveMigration(true).build()

        @Provides
        fun provideHabitDao(db: HabitHubDatabase): HabitDao = db.habitDao()

        @Provides
        fun provideHabitCompletionDao(db: HabitHubDatabase): HabitCompletionDao =
            db.habitCompletionDao()
    }
}