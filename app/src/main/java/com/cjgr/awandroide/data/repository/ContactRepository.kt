package com.cjgr.awandroide.data.repository

import com.cjgr.awandroide.data.local.ContactDao
import com.cjgr.awandroide.data.local.ContactEntity

class ContactRepository(private val contactDao: ContactDao) {

    suspend fun agregar(contact: ContactEntity): Long =
        contactDao.insertContact(contact)

    suspend fun actualizar(contact: ContactEntity) =
        contactDao.updateContact(contact)

    suspend fun eliminar(contact: ContactEntity) =
        contactDao.deleteContact(contact)

    suspend fun listarPorUsuario(userId: Int): List<ContactEntity> =
        contactDao.getContactsByUser(userId)

    suspend fun buscarPorCorreo(userId: Int, correo: String): ContactEntity? =
        contactDao.getContactByEmail(userId, correo)

    suspend fun buscarPorId(contactId: Int): ContactEntity? =
        contactDao.getContactById(contactId)
}