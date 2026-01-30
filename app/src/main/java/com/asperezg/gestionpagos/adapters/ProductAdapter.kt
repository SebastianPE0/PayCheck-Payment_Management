package com.asperezg.gestionpagos.adapters

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.models.Product
import com.asperezg.gestionpagos.models.CartItem
import com.asperezg.gestionpagos.controllers.CartController

class ProductAdapter(private val lista: List<Product>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreProd)
        val precio: TextView = view.findViewById(R.id.tvPrecioProd)
        val stock: TextView = view.findViewById(R.id.tvStockProd) // Nuevo campo
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = lista[position]
        holder.nombre.text = p.nombre
        holder.precio.text = "$${p.precioContado}"
        holder.stock.text = "Stock: ${p.stock}"

        // El clic ahora podría ser en toda la fila para Editar
        holder.itemView.setOnClickListener {
            // Lógica de edición próximamente
        }
    }
    //

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false))



    override fun getItemCount() = lista.size
}