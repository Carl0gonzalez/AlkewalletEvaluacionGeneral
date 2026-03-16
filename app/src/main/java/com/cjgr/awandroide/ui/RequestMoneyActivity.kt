package com.cjgr.awandroide.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cjgr.awandroide.R

class RequestMoneyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_money)
        findViewById<Button>(R.id.btnEnviarDineroConfirm).setOnClickListener {
            Toast.makeText(this, "Envío simulado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
