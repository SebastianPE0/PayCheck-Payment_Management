package com.asperezg.gestionpagos.views

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.adapters.CartAdapter
import com.asperezg.gestionpagos.controllers.CartController
import com.asperezg.gestionpagos.models.Solicitud
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val rvCarrito = findViewById<RecyclerView>(R.id.rvCarrito)
        val tvTotal = findViewById<TextView>(R.id.tvTotalCarrito)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmarCredito)

        fun actualizarUI() {
            tvTotal.text = "Total a Solicitar: $${CartController.obtenerTotal()}"
        }

        rvCarrito.layoutManager = LinearLayoutManager(this)
        // Pasamos la lista y la función para actualizar el total
        rvCarrito.adapter = CartAdapter(CartController.obtenerItems().toMutableList()) {
            actualizarUI()
        }

        actualizarUI()

        btnConfirmar.setOnClickListener {
            enviarSolicitudDeCredito()
        }
    }

    private fun enviarSolicitudDeCredito() {
        val uid = auth.currentUser?.uid ?: return
        val items = CartController.obtenerItems()

        if (items.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Validación de Stock Local previa para ahorrar recursos de red
        for (item in items) {
            if (item.cantidad > item.producto.stock) {
                Toast.makeText(this, "No hay stock suficiente de ${item.producto.nombre} (Disponible: ${item.producto.stock})", Toast.LENGTH_LONG).show()
                return
            }
        }

        db.collection("Usuarios").document(uid).get().addOnSuccessListener { userDoc ->
            val nombre = userDoc.getString("nombre") ?: "Usuario"
            val correo = userDoc.getString("correo") ?: ""

            // 2. Iniciamos un Batch para que la reserva y la solicitud sean una sola operación
            val batch = db.batch()
            val docSolicitudRef = db.collection("Solicitudes").document()

            // 3. Reservar Stock: Descontamos de la colección Productos
            items.forEach { item ->
                val productoRef = db.collection("Productos").document(item.producto.id)
                // Calculamos el nuevo stock restando la cantidad solicitada
                val nuevoStock = item.producto.stock - item.cantidad
                batch.update(productoRef, "stock", nuevoStock)
            }

            // 4. Crear el objeto de solicitud con ID del documento generado
            val nuevaSolicitud = Solicitud(
                id = docSolicitudRef.id,
                idCliente = uid,
                nombreCliente = nombre,
                correoCliente = correo,
                productos = items,
                total = CartController.obtenerTotal(),
                estado = "pendiente"
            )

            batch.set(docSolicitudRef, nuevaSolicitud)

            // 5. Ejecutar todas las operaciones juntas
            batch.commit().addOnSuccessListener {
                Toast.makeText(this, "Solicitud enviada y stock reservado", Toast.LENGTH_LONG).show()
                CartController.limpiarCarrito()
                finish()
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error al procesar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }


    }
}