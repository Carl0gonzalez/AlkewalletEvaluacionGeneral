package com.cjgr.awandroide.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cjgr.awandroide.R
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.cjgr.awandroide.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        lifecycleScope.launch {
            try {
                val users = withContext(Dispatchers.IO) {
                    RetrofitClient.api.getUsers()
                }
                // Por ahora solo logueamos en consola y mostramos un Toast
                android.util.Log.d("HomeActivity", "Usuarios desde API: $users")
                Toast.makeText(
                    this@HomeActivity,
                    "Usuarios API: ${users.size}",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                android.util.Log.e("HomeActivity", "Error API", e)
                Toast.makeText(
                    this@HomeActivity,
                    "Error API: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
