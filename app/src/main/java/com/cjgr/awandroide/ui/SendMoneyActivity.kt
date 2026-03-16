package com.cjgr.awandroide.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cjgr.awandroide.R

class SendMoneyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_money)
        findViewById<Button>(R.id.btnIngresarDineroConfirm).setOnClickListener {
            Toast.makeText(this, "Ingreso simulado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
