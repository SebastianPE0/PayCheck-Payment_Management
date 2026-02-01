package com.asperezg.gestionpagos.views

import android.os.Bundle
import android.widget.*
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
    private lateinit var spinnerCuotas: Spinner // Selector de meses

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val rvCarrito = findViewById<RecyclerView>(R.id.rvCarrito)
        val tvTotal = findViewById<TextView>(R.id.tvTotalCarrito)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmarCredito)
        spinnerCuotas = findViewById(R.id.spinnerCuotas)

        // Configurar las opciones de cuotas (3, 6, 9, 12 meses)
        val opcionesCuotas = arrayOf(1, 3, 6, 9, 12)
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesCuotas)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCuotas.adapter = adapterSpinner

        fun actualizarUI() {
            val total = CartController.obtenerTotal()
            tvTotal.text = "Total a Solicitar: $${String.format("%.2f", total)}"
        }

        rvCarrito.layoutManager = LinearLayoutManager(this)
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

        // 1. Validación de Stock Local
        for (item in items) {
            if (item.cantidad > item.producto.stock) {
                Toast.makeText(this, "Stock insuficiente: ${item.producto.nombre}", Toast.LENGTH_LONG).show()
                return
            }
        }

        val cuotasSeleccionadas = spinnerCuotas.selectedItem as Int
        val total = CartController.obtenerTotal()
        val montoPorCuota = total / cuotasSeleccionadas // Cálculo del plan

        db.collection("Usuarios").document(uid).get().addOnSuccessListener { userDoc ->
            val nombre = userDoc.getString("nombre") ?: "Usuario"
            val correo = userDoc.getString("correo") ?: ""

            // 2. Uso de Batch para asegurar la reserva de stock y creación de solicitud
            val batch = db.batch()
            val docSolicitudRef = db.collection("Solicitudes").document()

            // 3. Reservar Stock
            items.forEach { item ->
                val productoRef = db.collection("Productos").document(item.producto.id)
                val nuevoStock = item.producto.stock - item.cantidad
                batch.update(productoRef, "stock", nuevoStock)
            }

            // 4. Crear Solicitud con Plan de Pagos
            val nuevaSolicitud = Solicitud(
                id = docSolicitudRef.id,
                idCliente = uid,
                nombreCliente = nombre,
                correoCliente = correo,
                productos = items,
                total = total,
                estado = "pendiente",
                numeroCuotas = cuotasSeleccionadas, // Guardamos el plazo
                montoCuota = montoPorCuota        // Guardamos la cuota mensual
            )

            batch.set(docSolicitudRef, nuevaSolicitud)

            batch.commit().addOnSuccessListener {
                Toast.makeText(this, "Solicitud enviada a $cuotasSeleccionadas meses", Toast.LENGTH_LONG).show()
                CartController.limpiarCarrito()
                finish()
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}