package com.asperezg.gestionpagos.adapters

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.models.Product

class ProductAdapter(
    private var listaOriginal: List<Product>,
    private val onProductClick: (Product) -> Unit // Evento para capturar el clic
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    // Lista que usaremos para mostrar los datos
    private var listaMostrar: List<Product> = listaOriginal

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreProd)
        val precio: TextView = view.findViewById(R.id.tvPrecioProd)
        val stock: TextView = view.findViewById(R.id.tvStockProd)
    }

    // Función vital para actualizar la lista desde Firebase sin duplicados
    fun actualizarLista(nuevaLista: List<Product>) {
        listaOriginal = nuevaLista
        listaMostrar = nuevaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = listaMostrar[position]

        holder.nombre.text = p.nombre
        holder.precio.text = "$${p.precioContado}"
        holder.stock.text = "Stock: ${p.stock}"

        // Al hacer clic en la tarjeta, ejecutamos la función de edición
        holder.itemView.setOnClickListener {
            onProductClick(p)
        }
    }

    override fun getItemCount() = listaMostrar.size
}