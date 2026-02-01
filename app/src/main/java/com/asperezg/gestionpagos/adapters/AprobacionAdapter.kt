package com.asperezg.gestionpagos.adapters

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.models.Solicitud

class AprobacionAdapter(
    private var lista: List<Solicitud>, // La lista inicial
    private val onAprobar: (Solicitud) -> Unit,
    private val onRechazar: (Solicitud) -> Unit
) : RecyclerView.Adapter<AprobacionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreSolicitante)
        val total: TextView = view.findViewById(R.id.tvTotalSolicitud)
        val btnAprobar: Button = view.findViewById(R.id.btnAprobar)
        val btnRechazar: Button = view.findViewById(R.id.btnRechazar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_solicitud, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = lista[position]
        holder.nombre.text = s.nombreCliente
        holder.total.text = "Total: $${String.format("%.2f", s.total)}"

        holder.btnAprobar.setOnClickListener { onAprobar(s) }
        holder.btnRechazar.setOnClickListener { onRechazar(s) }
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<Solicitud>) {
        this.lista = nuevaLista
        notifyDataSetChanged() // Esto refresca la pantalla
    }
}