package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ChatDao
import com.example.data.local.dao.LocalAvatarDao
import com.example.data.local.dao.PersonaDao
import com.example.data.local.dao.SwarmLogDao
import com.example.data.local.entities.*
import com.example.data.local.entity.LocalAvatarEntity

@Database(
    entities =
        [
            ChatMessageEntity::class,
            PersonaEntity::class,
            SwarmLogEntity::class,
            UserEntity::class,
            MediaAssetEntity::class,
            LocalAvatarEntity::class,
        ],
    version = 2,
    exportSchema = false,
)
abstract class AuraDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    abstract fun personaDao(): PersonaDao

    abstract fun swarmLogDao(): SwarmLogDao

    abstract fun localAvatarDao(): LocalAvatarDao

    companion object {
        @Volatile private var INSTANCE: AuraDatabase? = null

        fun getInstance(context: Context): AuraDatabase {
            return INSTANCE
                ?: synchronized(this) {
                    val instance =
                        Room.databaseBuilder(
                            context.applicationContext,
                            AuraDatabase::class.java,
                            "aura_studio.db",
                        )
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                    instance
                }
        }
    }
}
