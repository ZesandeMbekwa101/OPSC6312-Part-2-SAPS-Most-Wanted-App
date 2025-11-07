package com.example.sapsmostwantedapp.di

import com.example.sapsmostwantedapp.data.dao.ReportDao
import com.example.sapsmostwantedapp.data.dao.UserDao
import com.example.sapsmostwantedapp.data.dao.WantedPersonNotificationDao
import com.example.sapsmostwantedapp.data.repository.AdminRepository
import com.example.sapsmostwantedapp.data.repository.AuthRepository
import com.example.sapsmostwantedapp.data.repository.ReportRepository
import com.example.sapsmostwantedapp.data.repository.WantedPersonNotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(userDao: UserDao): AuthRepository {
        return AuthRepository(userDao)
    }

    @Provides
    @Singleton
    fun provideAdminRepository(userDao: UserDao, reportDao: ReportDao): AdminRepository {
        return AdminRepository(userDao, reportDao)
    }

    @Provides
    @Singleton
    fun provideReportRepository(reportDao: ReportDao): ReportRepository {
        return ReportRepository(reportDao)
    }

    @Provides
    @Singleton
    fun provideWantedPersonNotificationRepository(notificationDao: WantedPersonNotificationDao): WantedPersonNotificationRepository {
        return WantedPersonNotificationRepository(notificationDao)
    }
}

