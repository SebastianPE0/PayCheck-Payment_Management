package com.asperezg.gestionpagos.adapters

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.models.User //

class UserAdapter(
    private var listaOriginal: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    // Esta es la lista que el RecyclerView realmente dibuja
    private var listaFiltrada: List<User> = listaOriginal

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreClienteRow)
        val estado: TextView = view.findViewById(R.id.tvEstadoCreditoRow)
    }

    // Función para actualizar la búsqueda en tiempo real
    fun filtrar(texto: String) {
        listaFiltrada = if (texto.isEmpty()) {
            listaOriginal
        } else {
            listaOriginal.filter { it.nombre.lowercase().contains(texto.lowercase()) }
        }
        notifyDataSetChanged() // Refresca la vista con el filtro
    }

    // Función para actualizar la lista completa cuando cambie Firebase
    fun actualizarLista(nuevaLista: List<User>) {
        listaOriginal = nuevaLista
        listaFiltrada = nuevaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_cliente, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val u = listaFiltrada[position] // Usamos la lista filtrada aquí

        holder.nombre.text = u.nombre

        // Lógica visual del estado de deuda
        if (u.tieneDeuda) {
            holder.estado.text = "Con Deuda"
            holder.estado.setTextColor(android.graphics.Color.RED)
        } else {
            holder.estado.text = "Al día"
            holder.estado.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
        }

        // Evento de selección para editar
        holder.itemView.setOnClickListener { onUserClick(u) }
    }

    override fun getItemCount() = listaFiltrada.size // Siempre retornar el tamaño de la filtrada
}