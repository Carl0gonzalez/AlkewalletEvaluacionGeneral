package com.cjgr.awandroide.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjgr.awandroide.data.local.ContactEntity
import com.cjgr.awandroide.data.repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ContactState {
    object Idle : ContactState()
    object Loading : ContactState()
    data class Success(val message: String) : ContactState()
    data class Error(val message: String) : ContactState()
}

class ContactViewModel(private val contactRepository: ContactRepository) : ViewModel() {

    private val _contactState = MutableStateFlow<ContactState>(ContactState.Idle)
    val contactState: StateFlow<ContactState> = _contactState

    private val _contactos = MutableStateFlow<List<ContactEntity>>(emptyList())
    val contactos: StateFlow<List<ContactEntity>> = _contactos

    fun cargarContactos(userId: Int) {
        viewModelScope.launch {
            try {
                _contactos.value = contactRepository.listarPorUsuario(userId)
            } catch (e: Exception) {
                _contactState.value = ContactState.Error("Error al cargar contactos")
            }
        }
    }

    fun agregarContacto(ownerUserId: Int, nombre: String, correo: String, alias: String = "") {
        viewModelScope.launch {
            _contactState.value = ContactState.Loading
            val n = nombre.trim(); val c = correo.trim()
            if (n.isEmpty() || c.isEmpty()) {
                _contactState.value = ContactState.Error("Nombre y correo son obligatorios")
                return@launch
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(c).matches()) {
                _contactState.value = ContactState.Error("Correo inválido")
                return@launch
            }
            val existente = contactRepository.buscarPorCorreo(ownerUserId, c)
            if (existente != null) {
                _contactState.value = ContactState.Error("Ya tienes un contacto con ese correo")
                return@launch
            }
            contactRepository.agregar(ContactEntity(ownerUserId = ownerUserId, nombre = n, correo = c, alias = alias.trim()))
            cargarContactos(ownerUserId)
            _contactState.value = ContactState.Success("Contacto agregado")
        }
    }

    fun editarContacto(contact: ContactEntity, nuevoNombre: String, nuevoCorreo: String, nuevoAlias: String = "") {
        viewModelScope.launch {
            _contactState.value = ContactState.Loading
            val n = nuevoNombre.trim(); val c = nuevoCorreo.trim()
            if (n.isEmpty() || c.isEmpty()) {
                _contactState.value = ContactState.Error("Nombre y correo son obligatorios")
                return@launch
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(c).matches()) {
                _contactState.value = ContactState.Error("Correo inválido")
                return@launch
            }
            val existente = contactRepository.buscarPorCorreo(contact.ownerUserId, c)
            if (existente != null && existente.id != contact.id) {
                _contactState.value = ContactState.Error("Ya tienes un contacto con ese correo")
                return@launch
            }
            contactRepository.actualizar(contact.copy(nombre = n, correo = c, alias = nuevoAlias.trim()))
            cargarContactos(contact.ownerUserId)
            _contactState.value = ContactState.Success("Contacto actualizado")
        }
    }

    fun eliminarContacto(contact: ContactEntity) {
        viewModelScope.launch {
            contactRepository.eliminar(contact)
            cargarContactos(contact.ownerUserId)
            _contactState.value = ContactState.Success("Contacto eliminado")
        }
    }

    fun resetState() { _contactState.value = ContactState.Idle }
}