package com.cjgr.awandroide.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cjgr.awandroide.R
import com.cjgr.awandroide.data.local.AppDatabase
import com.cjgr.awandroide.data.repository.ContactRepository
import com.cjgr.awandroide.data.repository.TransactionRepository
import com.cjgr.awandroide.data.repository.UserRepository
import com.cjgr.awandroide.network.RetrofitClient
import com.cjgr.awandroide.ui.viewmodel.AuthViewModel
import com.cjgr.awandroide.ui.viewmodel.ContactViewModel
import com.cjgr.awandroide.ui.viewmodel.TransactionState
import com.cjgr.awandroide.ui.viewmodel.TransactionViewModel
import com.cjgr.awandroide.ui.viewmodel.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SendMoneyActivity : AppCompatActivity() {

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var contactViewModel: ContactViewModel
    private var userId: Int = -1
    private var selectedCorreo: String = ""
    private var modoManual: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_money)

        userId = intent.getIntExtra("userId", -1)

        val db          = AppDatabase.getDatabase(this)
        val userRepo    = UserRepository(db.userDao())
        val txRepo      = TransactionRepository(db.transactionDao(), RetrofitClient.api)
        val contactRepo = ContactRepository(db.contactDao())
        val factory     = ViewModelFactory(userRepo, txRepo, contactRepo)

        transactionViewModel = ViewModelProvider(this, factory)[TransactionViewModel::class.java]
        authViewModel        = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        contactViewModel     = ViewModelProvider(this, factory)[ContactViewModel::class.java]

        val imgBack              = findViewById<ImageView>(R.id.imgBack)
        val txtNombre            = findViewById<TextView>(R.id.txtNombreUsuario)
        val txtCorreo            = findViewById<TextView>(R.id.txtCorreoUsuario)
        val imgFotoRemitente     = findViewById<ImageView>(R.id.imgFotoRemitente)
        val txtDestinatarioSel   = findViewById<TextView>(R.id.txtDestinatarioSeleccionado)
        val btnSeleccionar        = findViewById<Button>(R.id.btnSeleccionarContacto)
        val btnNuevoContacto      = findViewById<Button>(R.id.btnNuevoContacto)
        val btnIngresarManual     = findViewById<Button>(R.id.btnIngresarCorreoManual)
        val layoutCorreoManual    = findViewById<View>(R.id.layoutCorreoManual)
        val edtCorreoManual       = findViewById<EditText>(R.id.edtCorreoManual)
        val edtMonto              = findViewById<EditText>(R.id.edtMonto)
        val edtNota               = findViewById<EditText>(R.id.edtNota)
        val btnEnviar             = findViewById<Button>(R.id.btnEnviarConfirm)

        imgBack.setOnClickListener { finish() }

        if (userId != -1) {
            authViewModel.cargarUsuario(userId)
            contactViewModel.cargarContactos(userId)
        }

        lifecycleScope.launch {
            authViewModel.currentUser.collect { user ->
                user?.let {
                    txtNombre.text = it.nombre
                    txtCorreo.text = it.correo
                    // Cargar foto circular del remitente con Picasso
                    if (!it.fotoPerfil.isNullOrEmpty()) {
                        Picasso.get()
                            .load(it.fotoPerfil)
                            .placeholder(R.drawable.ic_profile)
                            .error(R.drawable.ic_profile)
                            .fit()
                            .centerCrop()
                            .into(imgFotoRemitente)
                    }
                }
            }
        }

        btnSeleccionar.setOnClickListener {
            val lista = contactViewModel.contactos.value
            if (lista.isEmpty()) {
                Toast.makeText(this, "No tienes contactos guardados.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val nombres = lista.map {
                if (it.alias.isNotEmpty()) "${it.alias} (${it.correo})"
                else "${it.nombre} (${it.correo})"
            }.toTypedArray()

            AlertDialog.Builder(this)
                .setTitle("Seleccionar destinatario")
                .setItems(nombres) { _, index ->
                    val sel = lista[index]
                    selectedCorreo = sel.correo
                    modoManual = false
                    layoutCorreoManual.visibility = View.GONE
                    txtDestinatarioSel.text =
                        if (sel.alias.isNotEmpty()) "${sel.alias} — ${sel.correo}"
                        else "${sel.nombre} — ${sel.correo}"
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnNuevoContacto.setOnClickListener {
            startActivity(
                Intent(this, ContactsActivity::class.java).putExtra("userId", userId)
            )
        }

        btnIngresarManual.setOnClickListener {
            modoManual = !modoManual
            if (modoManual) {
                layoutCorreoManual.visibility = View.VISIBLE
                selectedCorreo = ""
                txtDestinatarioSel.text = "Ingresando correo manualmente"
                btnIngresarManual.text = "Cancelar ingreso manual"
            } else {
                layoutCorreoManual.visibility = View.GONE
                edtCorreoManual.setText("")
                txtDestinatarioSel.text = "Ningún contacto seleccionado"
                btnIngresarManual.text = "Ingresar correo manualmente"
            }
        }

        btnEnviar.setOnClickListener {
            val correoFinal = if (modoManual) edtCorreoManual.text.toString().trim() else selectedCorreo

            if (correoFinal.isEmpty()) {
                Toast.makeText(this, "Selecciona o ingresa un destinatario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correoFinal).matches()) {
                Toast.makeText(this, "El correo del destinatario no es válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val correoPropio = txtCorreo.text.toString().trim()
            if (correoFinal.equals(correoPropio, ignoreCase = true)) {
                Toast.makeText(this, "No puedes transferirte dinero a ti mismo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val monto = edtMonto.text.toString().trim().toDoubleOrNull()
            if (monto == null || monto <= 0) {
                Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val nota  = edtNota.text.toString().trim().ifEmpty { "Transferencia" }

            transactionViewModel.realizarTransferencia(
                userId             = userId,
                destinatarioCorreo = correoFinal,
                monto              = monto,
                fecha              = fecha,
                descripcion        = nota,
                userRepository     = UserRepository(AppDatabase.getDatabase(this).userDao())
            )
        }

        lifecycleScope.launch {
            transactionViewModel.transactionState.collect { state ->
                when (state) {
                    is TransactionState.Success -> {
                        Toast.makeText(this@SendMoneyActivity, state.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is TransactionState.Error -> {
                        Toast.makeText(this@SendMoneyActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (userId != -1) contactViewModel.cargarContactos(userId)
    }
}
