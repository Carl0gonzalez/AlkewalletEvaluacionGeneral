package com.cjgr.awandroide.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cjgr.awandroide.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        findViewById<Button>(R.id.btnEnviarDinero).setOnClickListener {
            startActivity(Intent(this, SendMoneyActivity::class.java))
        }
        findViewById<Button>(R.id.btnIngresarDinero).setOnClickListener {
            startActivity(Intent(this, RequestMoneyActivity::class.java))
        }
    }
}
