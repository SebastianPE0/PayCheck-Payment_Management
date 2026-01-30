package com.asperezg.gestionpagos.views

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.adapters.ProductAdapter
import com.asperezg.gestionpagos.models.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class GestionProductosActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var rvProductos: RecyclerView
    private lateinit var adapter: ProductAdapter
    private val listaProductos = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_productos)

        rvProductos = findViewById(R.id.rvProductosAdmin)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddProducto)

        // Configuración del RecyclerView para mostrar el inventario
        rvProductos.layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter(listaProductos)
        rvProductos.adapter = adapter

        fabAdd.setOnClickListener {
            mostrarDialogoCrear()
        }

        consultarProductos()
    }

    private fun consultarProductos() {
        // SnapshotListener mantiene la lista actualizada sin recargar la pantalla
        db.collection("Productos")
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener

                // ESTO ES VITAL: Limpia la lista actual para que no se acumule con la anterior
                listaProductos.clear()

                if (value != null) {
                    for (doc in value) {
                        val producto = doc.toObject(Product::class.java)
                        if (producto != null) {
                            listaProductos.add(producto)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }


                listaProductos.clear()
                if (value != null) {
                    for (doc in value) {
                        // Mapeo automático de Firestore al modelo Product
                        val producto = doc.toObject(Product::class.java)
                        if (producto != null) {
                            listaProductos.add(producto)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }

    }

    private fun mostrarDialogoCrear() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val vista = layoutInflater.inflate(R.layout.dialog_crear_producto, null)
        builder.setView(vista)

        builder.setPositiveButton("Guardar") { _, _ ->
            val nombre = vista.findViewById<EditText>(R.id.etNombreDialog).text.toString()
            val precio = vista.findViewById<EditText>(R.id.etPrecioDialog).text.toString().toDoubleOrNull() ?: 0.0
            val stock = vista.findViewById<EditText>(R.id.etStockDialog).text.toString().toIntOrNull() ?: 0

            if (nombre.isNotEmpty() && precio > 0) {
                val docRef = db.collection("Productos").document()
                // Creación del objeto Product con el ID generado
                val p = Product(id = docRef.id, nombre = nombre, precioContado = precio, stock = stock)

                docRef.set(p).addOnSuccessListener {
                    Toast.makeText(this, "Producto '$nombre' guardado", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Nombre y precio son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
}