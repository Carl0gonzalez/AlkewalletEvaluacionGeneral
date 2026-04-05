package com.cjgr.awandroide.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cjgr.awandroide.R
import com.cjgr.awandroide.controller.WalletController
import com.cjgr.awandroide.controller.WalletSendResult

class RequestMoneyActivity : AppCompatActivity() {

    private lateinit var controller: WalletController
    private lateinit var edtMontoEnviar: EditText
    private lateinit var edtNotaEnviar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_money)

        controller = WalletController(this)

        edtMontoEnviar = findViewById(R.id.edtMontoEnviar)
        edtNotaEnviar = findViewById(R.id.edtNotaEnviar)

        findViewById<ImageView>(R.id.imgBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnEnviarDineroConfirm).setOnClickListener {
            onConfirmarEnvio()
        }
    }

    private fun onConfirmarEnvio() {
        val montoTexto = edtMontoEnviar.text.toString().replace(",", ".").trim()
        val nota = edtNotaEnviar.text.toString().trim()

        if (montoTexto.isEmpty()) {
            Toast.makeText(this, "Ingresa un monto", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoTexto.toDoubleOrNull()
        if (monto == null || monto <= 0.0) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show()
            return
        }

        when (controller.send(monto, nota)) {
            WalletSendResult.SUCCESS -> {
                Toast.makeText(this, "Dinero enviado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            WalletSendResult.INSUFFICIENT_FUNDS -> {
                Toast.makeText(this, "Saldo insuficiente", Toast.LENGTH_SHORT).show()
            }
            WalletSendResult.INVALID_AMOUNT -> {
                Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}