package com.cjgr.awandroide.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cjgr.awandroide.R
import com.cjgr.awandroide.data.local.AppDatabase
import com.cjgr.awandroide.data.local.ContactEntity
import com.cjgr.awandroide.data.repository.ContactRepository
import com.cjgr.awandroide.data.repository.TransactionRepository
import com.cjgr.awandroide.data.repository.UserRepository
import com.cjgr.awandroide.network.RetrofitClient
import com.cjgr.awandroide.ui.adapter.ContactsAdapter
import com.cjgr.awandroide.ui.viewmodel.ContactState
import com.cjgr.awandroide.ui.viewmodel.ContactViewModel
import com.cjgr.awandroide.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class ContactsActivity : AppCompatActivity() {

    private lateinit var contactViewModel: ContactViewModel
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        userId = intent.getIntExtra("userId", -1)

        val db = AppDatabase.getDatabase(this)
        val factory = ViewModelFactory(
            UserRepository(db.userDao()),
            TransactionRepository(db.transactionDao(), RetrofitClient.api),
            ContactRepository(db.contactDao())
        )
        contactViewModel = ViewModelProvider(this, factory)[ContactViewModel::class.java]

        val imgBack   = findViewById<ImageView>(R.id.imgBack)
        val btnAgregar = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAgregarContacto)
        val recycler  = findViewById<RecyclerView>(R.id.recyclerContactos)

        imgBack.setOnClickListener { finish() }

        val adapter = ContactsAdapter(
            onEdit = { contact -> mostrarDialogoEditar(contact) },
            onDelete = { contact ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar contacto")
                    .setMessage("¿Eliminar a ${contact.nombre}?")
                    .setPositiveButton("Eliminar") { _, _ -> contactViewModel.eliminarContacto(contact) }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        btnAgregar.setOnClickListener { mostrarDialogoAgregar() }

        lifecycleScope.launch {
            contactViewModel.contactos.collect { lista ->
                adapter.submitList(lista)
            }
        }

        lifecycleScope.launch {
            contactViewModel.contactState.collect { state ->
                when (state) {
                    is ContactState.Success -> {
                        Toast.makeText(this@ContactsActivity, state.message, Toast.LENGTH_SHORT).show()
                        contactViewModel.resetState()
                    }
                    is ContactState.Error -> {
                        Toast.makeText(this@ContactsActivity, state.message, Toast.LENGTH_SHORT).show()
                        contactViewModel.resetState()
                    }
                    else -> Unit
                }
            }
        }

        if (userId != -1) contactViewModel.cargarContactos(userId)
    }

    private fun mostrarDialogoAgregar() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_contact_form, null)
        val edtNombre = view.findViewById<EditText>(R.id.edtContactNombre)
        val edtCorreo = view.findViewById<EditText>(R.id.edtContactCorreo)
        val edtAlias  = view.findViewById<EditText>(R.id.edtContactAlias)

        AlertDialog.Builder(this)
            .setTitle("Nuevo contacto")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                contactViewModel.agregarContacto(
                    userId,
                    edtNombre.text.toString(),
                    edtCorreo.text.toString(),
                    edtAlias.text.toString()
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEditar(contact: ContactEntity) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_contact_form, null)
        val edtNombre = view.findViewById<EditText>(R.id.edtContactNombre)
        val edtCorreo = view.findViewById<EditText>(R.id.edtContactCorreo)
        val edtAlias  = view.findViewById<EditText>(R.id.edtContactAlias)

        edtNombre.setText(contact.nombre)
        edtCorreo.setText(contact.correo)
        edtAlias.setText(contact.alias)

        AlertDialog.Builder(this)
            .setTitle("Editar contacto")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                contactViewModel.editarContacto(
                    contact,
                    edtNombre.text.toString(),
                    edtCorreo.text.toString(),
                    edtAlias.text.toString()
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}