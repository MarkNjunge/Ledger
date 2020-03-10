package com.marknjunge.ledger.data.local

import androidx.paging.DataSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.marknjunge.ledger.data.models.MpesaMessageEntity

@Dao
interface MessagesDao {
    @Query("SELECT * from mpesa_messages ORDER BY transaction_date DESC")
    suspend fun getAll(): List<MpesaMessageEntity>

    // The Int type parameter tells Room to use a PositionalDataSource object.
    @Query("SELECT * FROM mpesa_messages ORDER BY transaction_date DESC")
    fun pagedMessagesByDate(): DataSource.Factory<Int, MpesaMessageEntity>

    @Query("SELECT * from mpesa_messages ORDER BY transaction_date DESC LIMIT 1")
    suspend fun getLatest(): MpesaMessageEntity?

    @Query("SELECT * from mpesa_messages WHERE body LIKE :term ORDER BY transaction_date DESC")
    fun search(term: String): DataSource.Factory<Int, MpesaMessageEntity>

    @RawQuery(observedEntities = [MpesaMessageEntity::class])
    fun filterPaged(query:SupportSQLiteQuery): DataSource.Factory<Int, MpesaMessageEntity>

    @RawQuery(observedEntities = [MpesaMessageEntity::class])
    suspend fun filter(query:SupportSQLiteQuery): List<MpesaMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mpesaMessageEntity: MpesaMessageEntity)
}
