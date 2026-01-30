package com.asperezg.gestionpagos.views

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.adapters.ProductAdapter
import com.asperezg.gestionpagos.controllers.CartController
import com.asperezg.gestionpagos.models.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ClienteActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente)

        val tvNombre = findViewById<TextView>(R.id.tvNombreCliente)
        val tvDeuda = findViewById<TextView>(R.id.tvEstadoDeuda)

        // Recuperamos el UID del usuario actual que inició sesión
        val uid = auth.currentUser?.uid ?: ""

        if (uid.isNotEmpty()) {
            // Consultamos los datos específicos del documento en Firestore
            db.collection("Usuarios").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val nombre = doc.getString("nombre")
                        val tieneDeuda = doc.getBoolean("tieneDeuda") ?: false

                        tvNombre.text = "Bienvenido, $nombre"
                        tvDeuda.text = if (tieneDeuda) "Estado: Tienes pagos pendientes" else "Estado: Al día"
                    }
                }
        }

        // Dentro de onCreate de ClienteActivity.kt
        val fabCarrito = findViewById<FloatingActionButton>(R.id.fabCarrito)

        fabCarrito.setOnClickListener {
            // Verificamos si hay items antes de ir al carrito
            if (CartController.obtenerItems().isNotEmpty()) {
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            }
        }



    }
    private fun cargarProductos() {
        val rv = findViewById<RecyclerView>(R.id.rvProductos)
        rv.layoutManager = LinearLayoutManager(this)

        db.collection("Productos").get()
            .addOnSuccessListener { result ->
                val productos = result.toObjects(Product::class.java)
                rv.adapter = ProductAdapter(productos)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar productos", Toast.LENGTH_SHORT).show()
            }
    }

}