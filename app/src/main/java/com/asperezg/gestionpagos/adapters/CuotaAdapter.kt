package com.asperezg.gestionpagos.adapters

import android.graphics.Color
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.models.Cuota
import java.text.SimpleDateFormat
import java.util.*

class CuotaAdapter(
    private var lista: List<Cuota>,
    private val onCuotaClick: (Cuota) -> Unit // 1. Agregamos el callback para el clic
) : RecyclerView.Adapter<CuotaAdapter.ViewHolder>() {

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
        holder.info.text = "${c.numeroCuota}/${c.totalCuotas}"
        holder.monto.text = "$${String.format("%.2f", c.monto)}"

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.fecha.text = "Vence: ${sdf.format(Date(c.fechaVencimiento))}"

        // 2. Lógica de estados y colores mejorada para tu tesis
        holder.estado.text = c.estado.uppercase()
        when (c.estado) {
            "pendiente" -> holder.estado.setTextColor(Color.parseColor("#F59E0B")) // Naranja
            "revision" -> holder.estado.setTextColor(Color.parseColor("#3F51B5"))  // Azul (esperando admin)
            "pagada" -> holder.estado.setTextColor(Color.parseColor("#2E7D32"))    // Verde
            else -> holder.estado.setTextColor(Color.GRAY)
        }

        // 3. Activar el clic solo si está pendiente
        holder.itemView.setOnClickListener {
            if (c.estado == "pendiente") {
                onCuotaClick(c)
            }
        }
    }

    override fun getItemCount() = lista.size

    fun actualizar(nuevaLista: List<Cuota>) {
        this.lista = nuevaLista
        notifyDataSetChanged()
    }
}