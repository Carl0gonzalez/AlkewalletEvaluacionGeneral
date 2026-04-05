package com.cjgr.awandroide.controller

import android.content.Context
import com.cjgr.awandroide.model.Transaction
import com.cjgr.awandroide.model.WalletRepository

/**
 * CONTROLADOR: orquesta la lógica entre la vista (Activities) y el modelo.
 */
class WalletController(private val context: Context) {

    fun getBalance(): Double = WalletRepository.getBalance(context)

    fun getTransactions(): List<Transaction> = WalletRepository.getTransactions()

    fun deposit(amount: Double, note: String): Boolean {
        if (amount <= 0.0) return false
        WalletRepository.deposit(context, amount, note)
        return true
    }

    fun send(amount: Double, note: String): WalletSendResult {
        if (amount <= 0.0) return WalletSendResult.INVALID_AMOUNT
        val tx = WalletRepository.send(context, amount, note)
        return if (tx == null) WalletSendResult.INSUFFICIENT_FUNDS else WalletSendResult.SUCCESS
    }
}