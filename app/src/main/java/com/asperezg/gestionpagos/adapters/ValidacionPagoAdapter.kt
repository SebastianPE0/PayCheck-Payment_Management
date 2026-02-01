package com.asperezg.gestionpagos.adapters

import android.graphics.Color
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.models.Cuota
import java.text.SimpleDateFormat
import java.util.*

class ValidacionPagoAdapter(
    private var lista: List<Cuota>,
    private val onCuotaClick: (Cuota) -> Unit
) : RecyclerView.Adapter<ValidacionPagoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val info: TextView = view.findViewById(R.id.tvInfoCuota)
        val monto: TextView = view.findViewById(R.id.tvMontoCuota)
        val fecha: TextView = view.findViewById(R.id.tvFechaVencimiento)
        val estado: TextView = view.findViewById(R.id.tvEstadoCuota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_cuota, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val c = lista[position]
        holder.info.text = "Cuota ${c.numeroCuota}/${c.totalCuotas}"
        holder.monto.text = "$${String.format("%.2f", c.monto)}"

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.fecha.text = "Notificado: ${sdf.format(Date(c.fechaNotificacion))}"

        holder.estado.text = "EN REVISIÓN"
        holder.estado.setTextColor(Color.parseColor("#3F51B5")) // Azul para destacar

        // AQUÍ EL CAMBIO: El admin SIEMPRE puede hacer clic
        holder.itemView.setOnClickListener { onCuotaClick(c) }
    }

    override fun getItemCount() = lista.size

    fun actualizar(nuevaLista: List<Cuota>) {
        this.lista = nuevaLista
        notifyDataSetChanged()
    }
}