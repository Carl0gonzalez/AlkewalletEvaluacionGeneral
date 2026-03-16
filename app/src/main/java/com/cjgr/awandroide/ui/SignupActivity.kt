package com.cjgr.awandroide.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cjgr.awandroide.R

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        findViewById<Button>(R.id.btnRegistrarse).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        findViewById<TextView>(R.id.btnIrLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
