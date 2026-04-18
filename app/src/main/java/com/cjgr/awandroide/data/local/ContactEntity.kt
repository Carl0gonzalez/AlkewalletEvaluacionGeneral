package com.cjgr.awandroide.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ownerUserId: Int,        // ID del usuario logueado dueño del contacto
    val nombre: String,
    val correo: String,
    val alias: String = ""       // apodo opcional
)