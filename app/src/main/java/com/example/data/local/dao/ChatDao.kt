package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE personaId = :personaId ORDER BY timestamp ASC")
    fun getMessagesByPersona(personaId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Query("DELETE FROM chat_messages WHERE personaId = :personaId")
    suspend fun deleteMessagesByPersona(personaId: String)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()
}
