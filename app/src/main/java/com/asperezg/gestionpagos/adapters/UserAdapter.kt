package com.asperezg.gestionpagos.adapters

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.models.User // Asegúrate de tener este modelo

class UserAdapter(private val lista: List<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreClienteRow)
        val estado: TextView = view.findViewById(R.id.tvEstadoCreditoRow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_cliente, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val u = lista[position]
        holder.nombre.text = u.nombre
        holder.estado.text = if (u.tieneDeuda) "Con Deuda" else "Al día"
        holder.estado.setTextColor(if (u.tieneDeuda) android.graphics.Color.RED else android.graphics.Color.parseColor("#2E7D32"))
    }

    override fun getItemCount() = lista.size
}