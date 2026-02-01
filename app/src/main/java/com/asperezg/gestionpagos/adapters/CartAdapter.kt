package com.asperezg.gestionpagos.adapters

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.models.CartItem
import com.asperezg.gestionpagos.controllers.CartController

class CartAdapter(
    private var items: MutableList<CartItem>,
    private val onTotalChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreCart)
        val precio: TextView = view.findViewById(R.id.tvPrecioCart)
        val cantidad: TextView = view.findViewById(R.id.tvCantidadCart)
        val btnMas: ImageButton = view.findViewById(R.id.btnMasCart)
        val btnMenos: ImageButton = view.findViewById(R.id.btnMenosCart)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminarCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carrito, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = holder.bindingAdapterPosition
        // Validación de seguridad para evitar errores de índice
        if (pos == RecyclerView.NO_POSITION) return

        val item = items[pos]

        // Error línea 34 y 35: Asegúrate de que 'producto' exista en CartItem
        holder.nombre.text = item.producto.nombre
        holder.precio.text = "$${String.format("%.2f", item.producto.precioContado * item.cantidad)}"
        holder.cantidad.text = item.cantidad.toString()

        holder.btnMas.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                items[currentPos].cantidad++
                notifyItemChanged(currentPos)
                onTotalChanged()
            }
        }

        holder.btnMenos.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION && items[currentPos].cantidad > 1) {
                items[currentPos].cantidad--
                notifyItemChanged(currentPos)
                onTotalChanged()
            }
        }

        holder.btnEliminar.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                // Error línea 56: Verifica que eliminarDelCarrito reciba un String (ID)
                val idProducto = items[currentPos].producto.id
                CartController.eliminarDelCarrito(idProducto)

                items.removeAt(currentPos)
                notifyItemRemoved(currentPos)
                notifyItemRangeChanged(currentPos, items.size)
                onTotalChanged()
            }
        }
    }

    override fun getItemCount() = items.size
}