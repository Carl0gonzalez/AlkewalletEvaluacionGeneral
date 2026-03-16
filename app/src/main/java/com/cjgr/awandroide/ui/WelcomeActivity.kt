package com.cjgr.awandroide.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cjgr.awandroide.R

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        findViewById<Button>(R.id.btnCrearCuenta).setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        findViewById<TextView>(R.id.btnYaTieneCuenta).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
