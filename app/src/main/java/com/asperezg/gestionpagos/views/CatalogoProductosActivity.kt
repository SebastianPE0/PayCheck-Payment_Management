package com.asperezg.gestionpagos.views

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.adapters.ProductAdapter
import com.asperezg.gestionpagos.controllers.CartController
import com.asperezg.gestionpagos.models.Product
import com.google.firebase.firestore.FirebaseFirestore

class CatalogoProductosActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: ProductAdapter
    private var listaOriginal = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo_productos)

        // Inicializamos el adaptador vacío para evitar errores antes de cargar datos
        adapter = ProductAdapter(mutableListOf()) { producto ->
            if (producto.stock > 0) {
                CartController.agregarAlCarrito(producto)
                Toast.makeText(this, "${producto.nombre} añadido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sin stock disponible", Toast.LENGTH_SHORT).show()
            }
        }

        val rv = findViewById<RecyclerView>(R.id.rvProductos)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        configurarBuscador()
        configurarBotonCarrito()
        escucharProductos() // Cambio a tiempo real
    }

    private fun configurarBuscador() {
        val svBuscador = findViewById<SearchView>(R.id.svBuscadorProductos)
        svBuscador.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText) // Filtra usando la lógica del adapter
                return true
            }
        })
    }

    private fun configurarBotonCarrito() {
        // Vinculamos al ImageButton que pusimos junto al buscador
        val btnCarrito = findViewById<ImageButton>(R.id.btnVerCarritoTop)
        btnCarrito.setOnClickListener {
            if (CartController.obtenerItems().isNotEmpty()) {
                startActivity(Intent(this, CartActivity::class.java))
            } else {
                Toast.makeText(this, "Tu carrito está vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun escucharProductos() {
        // Usamos addSnapshotListener para sincronización automática de stock
        db.collection("Productos").addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, "Error al sincronizar", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (value != null) {
                listaOriginal = value.toObjects(Product::class.java).toMutableList()
                adapter.actualizarLista(listaOriginal) // Función que agregamos al adapter
            }
        }
    }
}