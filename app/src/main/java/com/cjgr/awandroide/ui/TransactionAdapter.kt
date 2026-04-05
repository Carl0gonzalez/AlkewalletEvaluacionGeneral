package com.cjgr.awandroide.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cjgr.awandroide.R
import com.cjgr.awandroide.model.Transaction
import com.cjgr.awandroide.model.TransactionType
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter(
    private var items: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    fun update(newItems: List<Transaction>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgIcon: ImageView = view.findViewById(R.id.imgTransactionIcon)
        private val txtTitle: TextView = view.findViewById(R.id.txtTransactionTitle)
        private val txtDate: TextView = view.findViewById(R.id.txtTransactionDate)
        private val txtAmount: TextView = view.findViewById(R.id.txtTransactionAmount)

        fun bind(tx: Transaction) {
            val clLocale = Locale("es", "CL")
            val formatter = NumberFormat.getCurrencyInstance(clLocale)
            val formattedAmount = formatter.format(tx.amount)

            when (tx.type) {
                TransactionType.INGRESO -> {
                    imgIcon.setImageResource(R.drawable.ic_request)
                    txtTitle.text = "Ingreso"
                    txtAmount.text = "+ $formattedAmount"
                    txtAmount.setTextColor(
                        itemView.context.getColor(R.color.colorVerde)
                    )
                }
                TransactionType.ENVIO -> {
                    imgIcon.setImageResource(R.drawable.ic_send)
                    txtTitle.text = "Envío"
                    txtAmount.text = "- $formattedAmount"
                    txtAmount.setTextColor(
                        itemView.context.getColor(android.R.color.holo_red_dark)
                    )
                }
            }

            txtDate.text = tx.date
        }
    }
}