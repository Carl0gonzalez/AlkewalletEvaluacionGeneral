package com.cjgr.awandroide.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity): Long

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)

    @Query("SELECT * FROM contacts WHERE ownerUserId = :userId ORDER BY nombre ASC")
    suspend fun getContactsByUser(userId: Int): List<ContactEntity>

    @Query("SELECT * FROM contacts WHERE ownerUserId = :userId AND correo = :correo LIMIT 1")
    suspend fun getContactByEmail(userId: Int, correo: String): ContactEntity?

    @Query("SELECT * FROM contacts WHERE id = :contactId LIMIT 1")
    suspend fun getContactById(contactId: Int): ContactEntity?
}