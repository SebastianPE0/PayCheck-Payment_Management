package com.asperezg.gestionpagos.adapters

import android.view.*
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.models.Product
import java.util.*

class ProductAdapter(
    private var listaOriginal: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>(), Filterable {

    // Esta es la lista que realmente se dibuja y se filtra
    private var listaMostrar: List<Product> = listaOriginal

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreProd)
        val precio: TextView = view.findViewById(R.id.tvPrecioProd)
        val stock: TextView = view.findViewById(R.id.tvStockProd)
        val btnAgregar: ImageButton = view.findViewById(R.id.btnAgregarAlCarrito) // Nuevo botón
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = listaMostrar[position]
        holder.nombre.text = p.nombre
        holder.precio.text = "$${String.format("%.2f", p.precioContado)}"

        // Lógica de colores para el stock
        if (p.stock <= 0) {
            holder.stock.text = "Agotado"
            holder.stock.setTextColor(android.graphics.Color.RED)
            holder.btnAgregar.isEnabled = false
            holder.btnAgregar.alpha = 0.3f // Se ve opaco si no hay stock
        } else {
            holder.stock.text = "Disponibles: ${p.stock}"
            holder.stock.setTextColor(android.graphics.Color.GRAY)
            holder.btnAgregar.isEnabled = true
            holder.btnAgregar.alpha = 1.0f
        }

        holder.btnAgregar.setOnClickListener { onProductClick(p) }
    }

    override fun getItemCount() = listaMostrar.size

    // LÓGICA DEL BUSCADOR
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val busqueda = constraint.toString().lowercase(Locale.getDefault())
                listaMostrar = if (busqueda.isEmpty()) {
                    listaOriginal
                } else {
                    listaOriginal.filter {
                        it.nombre.lowercase(Locale.getDefault()).contains(busqueda)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = listaMostrar
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listaMostrar = results?.values as List<Product>
                notifyDataSetChanged()
            }
        }
    }
    // Agrégalo debajo de getItemCount()
    fun actualizarLista(nuevaLista: List<Product>) {
        this.listaOriginal = nuevaLista
        this.listaMostrar = nuevaLista
        notifyDataSetChanged() // Refresca visualmente el RecyclerView
    }
}