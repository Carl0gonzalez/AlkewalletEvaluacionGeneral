package com.cjgr.awandroide.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cjgr.awandroide.R
import com.cjgr.awandroide.data.local.ContactEntity

class ContactsAdapter(
    private val onEdit: (ContactEntity) -> Unit,
    private val onDelete: (ContactEntity) -> Unit
) : ListAdapter<ContactEntity, ContactsAdapter.ContactViewHolder>(DiffCallback) {

    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView  = view.findViewById(R.id.txtContactNombre)
        val txtCorreo: TextView  = view.findViewById(R.id.txtContactCorreo)
        val txtAlias: TextView   = view.findViewById(R.id.txtContactAlias)
        val btnEditar: ImageView = view.findViewById(R.id.btnEditarContacto)
        val btnEliminar: ImageView = view.findViewById(R.id.btnEliminarContacto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.txtNombre.text  = contact.nombre
        holder.txtCorreo.text  = contact.correo
        holder.txtAlias.text   = if (contact.alias.isNotEmpty()) contact.alias else ""
        holder.txtAlias.visibility = if (contact.alias.isNotEmpty()) View.VISIBLE else View.GONE
        holder.btnEditar.setOnClickListener  { onEdit(contact) }
        holder.btnEliminar.setOnClickListener { onDelete(contact) }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ContactEntity>() {
        override fun areItemsTheSame(a: ContactEntity, b: ContactEntity) = a.id == b.id
        override fun areContentsTheSame(a: ContactEntity, b: ContactEntity) = a == b
    }
}