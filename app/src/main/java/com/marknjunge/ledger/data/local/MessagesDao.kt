package com.marknjunge.ledger.data.local

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.marknjunge.ledger.data.models.MpesaMessageEntity

@Dao
interface MessagesDao {
    @Query("SELECT * from mpesa_messages")
    suspend fun getAll(): List<MpesaMessageEntity>

    // The Int type parameter tells Room to use a PositionalDataSource object.
    @Query("SELECT * FROM mpesa_messages ORDER BY transaction_date DESC")
    fun pagedMessagesByDate(): DataSource.Factory<Int, MpesaMessageEntity>

    @Query("SELECT * from mpesa_messages ORDER BY transaction_date DESC LIMIT 1")
    suspend fun getLatest(): MpesaMessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mpesaMessageEntity: MpesaMessageEntity)
}
