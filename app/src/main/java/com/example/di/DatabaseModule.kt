package com.example.di

import android.content.Context
import com.example.data.local.LocalAvatarDatabase
import com.example.data.local.dao.LocalAvatarDao
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
    fun provideLocalAvatarDatabase(
        @ApplicationContext context: Context,
    ): LocalAvatarDatabase {
        return LocalAvatarDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideLocalAvatarDao(database: LocalAvatarDatabase): LocalAvatarDao {
        return database.localAvatarDao()
    }
}
