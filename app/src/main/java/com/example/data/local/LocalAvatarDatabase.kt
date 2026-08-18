package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.LocalAvatarDao
import com.example.data.local.entity.LocalAvatarEntity

@Database(entities = [LocalAvatarEntity::class], version = 1, exportSchema = false)
abstract class LocalAvatarDatabase : RoomDatabase() {

  abstract fun localAvatarDao(): LocalAvatarDao

  companion object {
    @Volatile private var INSTANCE: LocalAvatarDatabase? = null

    fun getDatabase(context: Context): LocalAvatarDatabase {
      return INSTANCE
          ?: synchronized(this) {
            val instance =
                Room.databaseBuilder(
                        context.applicationContext,
                        LocalAvatarDatabase::class.java,
                        "local_avatar_database")
                    .build()
            INSTANCE = instance
            instance
          }
    }
  }
}
