package com.cjgr.awandroide.model

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * MODELO: mantiene el saldo y la lista de transacciones.
 */
object WalletRepository {

    private const val PREFS_NAME = "wallet_prefs"
    private const val KEY_BALANCE = "balance"

    private var balance: Double = 0.0
    private val transactions = mutableListOf<Transaction>()
    private var nextId: Int = 1
    private var initialized: Boolean = false

    private fun ensureInitialized(context: Context) {
        if (initialized) return

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        balance = prefs.getFloat(KEY_BALANCE, 0f).toDouble()
        initialized = true
    }

    private fun persistBalance(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putFloat(KEY_BALANCE, balance.toFloat())
            .apply()
    }

    fun getBalance(context: Context): Double {
        ensureInitialized(context)
        return balance
    }

    fun getTransactions(): List<Transaction> = transactions.toList()

    fun deposit(context: Context, amount: Double, note: String): Transaction {
        ensureInitialized(context)

        require(amount > 0.0) { "El monto a ingresar debe ser mayor a 0" }

        balance += amount
        val transaction = Transaction(
            id = nextId++,
            type = TransactionType.INGRESO,
            amount = amount,
            note = note,
            date = today()
        )
        transactions.add(0, transaction)
        persistBalance(context)
        return transaction
    }

    fun send(context: Context, amount: Double, note: String): Transaction? {
        ensureInitialized(context)

        require(amount > 0.0) { "El monto a enviar debe ser mayor a 0" }

        if (amount > balance) {
            return null
        }

        balance -= amount
        val transaction = Transaction(
            id = nextId++,
            type = TransactionType.ENVIO,
            amount = amount,
            note = note,
            date = today()
        )
        transactions.add(0, transaction)
        persistBalance(context)
        return transaction
    }

    private fun today(): String {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.format(Date())
    }
}