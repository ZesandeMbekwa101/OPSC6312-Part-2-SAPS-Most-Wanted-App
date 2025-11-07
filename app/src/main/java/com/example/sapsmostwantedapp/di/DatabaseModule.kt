package com.example.sapsmostwantedapp.di

import android.content.Context
import com.example.sapsmostwantedapp.data.dao.ActivityLogDao
import com.example.sapsmostwantedapp.data.dao.ReportDao
import com.example.sapsmostwantedapp.data.dao.UserDao
import com.example.sapsmostwantedapp.data.dao.WantedPersonNotificationDao
import com.example.sapsmostwantedapp.data.storage.JsonStorageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideJsonStorageManager(@ApplicationContext context: Context): JsonStorageManager {
        return try {
            JsonStorageManager(context.applicationContext)
        } catch (e: Exception) {
            android.util.Log.e("DatabaseModule", "Failed to create JsonStorageManager: ${e.message}", e)
            throw e
        }
    }

    @Provides
    @Singleton
    fun provideUserDao(storageManager: JsonStorageManager): UserDao {
        return UserDao(storageManager)
    }

    @Provides
    @Singleton
    fun provideReportDao(storageManager: JsonStorageManager): ReportDao {
        return ReportDao(storageManager)
    }

    @Provides
    @Singleton
    fun provideActivityLogDao(storageManager: JsonStorageManager): ActivityLogDao {
        return ActivityLogDao(storageManager)
    }

    @Provides
    @Singleton
    fun provideWantedPersonNotificationDao(storageManager: JsonStorageManager): WantedPersonNotificationDao {
        return WantedPersonNotificationDao(storageManager)
    }
}

