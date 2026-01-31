package com.asperezg.gestionpagos.views

import android.os.Bundle
import android.widget.Button
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

        rvProductos.layoutManager = LinearLayoutManager(this)

        // Inicializamos el adaptador con la función para EDITAR
        adapter = ProductAdapter(listaProductos) { productoSeleccionado ->
            mostrarDialogoEditar(productoSeleccionado)
        }
        rvProductos.adapter = adapter

        fabAdd.setOnClickListener { mostrarDialogoCrear() }
        consultarProductos()
    }

    private fun consultarProductos() {
        db.collection("Productos")
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener

                val listaNueva = mutableListOf<Product>()
                if (value != null) {
                    for (doc in value) {
                        val producto = doc.toObject(Product::class.java)
                        if (producto != null) listaNueva.add(producto)
                    }
                    // Usamos la función del adaptador para evitar duplicados
                    adapter.actualizarLista(listaNueva)
                }
            }
    }

    private fun mostrarDialogoEditar(p: Product) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val vista = layoutInflater.inflate(R.layout.dialog_editar_producto, null)

        // 1. Referencias de los campos de texto
        val etNombre = vista.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNombreEditProd)
        val etPrecio = vista.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPrecioEditProd)
        val etStock = vista.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etStockEditProd)

        // 2. Referencias de los botones del layout
        val btnActualizar = vista.findViewById<Button>(R.id.btnActualizarProd)
        val btnCancelar = vista.findViewById<Button>(R.id.btnCancelarEditProd)
        val btnEliminar = vista.findViewById<Button>(R.id.btnEliminarProducto)

        // 3. Cargar datos actuales del producto
        etNombre.setText(p.nombre)
        etPrecio.setText(p.precioContado.toString())
        etStock.setText(p.stock.toString())

        builder.setView(vista)
        val dialogActual = builder.create()

        // 4. Lógica del botón Actualizar
        btnActualizar.setOnClickListener {
            val nuevoNombre = etNombre.text.toString()
            val nuevoPrecio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0
            val nuevoStock = etStock.text.toString().toIntOrNull() ?: 0

            if (nuevoNombre.isNotEmpty() && nuevoPrecio > 0) {
                db.collection("Productos").document(p.id)
                    .update(mapOf(
                        "nombre" to nuevoNombre,
                        "precioContado" to nuevoPrecio,
                        "stock" to nuevoStock
                    ))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
                        dialogActual.dismiss() // Cerramos el diálogo manualmente
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Por favor completa los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }

        // 5. Lógica del botón Cancelar
        btnCancelar.setOnClickListener {
            dialogActual.dismiss()
        }

        // 6. Lógica del botón Eliminar
        btnEliminar.setOnClickListener {
            confirmarEliminacion(p, dialogActual)
        }

        dialogActual.show()
    }

    private fun confirmarEliminacion(p: Product, editDialog: androidx.appcompat.app.AlertDialog) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("¿Eliminar ${p.nombre}?")
            .setMessage("Esta acción quitará el producto del inventario.")
            .setPositiveButton("Eliminar") { _, _ ->
                db.collection("Productos").document(p.id).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
                        editDialog.dismiss() // Cierra el diálogo de edición
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
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
                val p = Product(id = docRef.id, nombre = nombre, precioContado = precio, stock = stock)
                docRef.set(p).addOnSuccessListener {
                    Toast.makeText(this, "Producto guardado", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
}