package com.cjgr.awandroide.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cjgr.awandroide.R
import com.cjgr.awandroide.controller.WalletController

class SendMoneyActivity : AppCompatActivity() {

    private lateinit var controller: WalletController
    private lateinit var edtMontoIngresar: EditText
    private lateinit var edtNota: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_money)

        controller = WalletController(this)

        edtMontoIngresar = findViewById(R.id.edtMontoIngresar)
        edtNota = findViewById(R.id.edtNota)

        findViewById<ImageView>(R.id.imgBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnIngresarDineroConfirm).setOnClickListener {
            onConfirmarIngreso()
        }
    }

    private fun onConfirmarIngreso() {
        val montoTexto = edtMontoIngresar.text.toString().replace(",", ".").trim()
        val nota = edtNota.text.toString().trim()

        if (montoTexto.isEmpty()) {
            Toast.makeText(this, "Ingresa un monto", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoTexto.toDoubleOrNull()
        if (monto == null || monto <= 0.0) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val ok = controller.deposit(monto, nota)
        if (ok) {
            Toast.makeText(this, "Dinero ingresado correctamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "No se pudo ingresar el dinero", Toast.LENGTH_SHORT).show()
        }
    }
}