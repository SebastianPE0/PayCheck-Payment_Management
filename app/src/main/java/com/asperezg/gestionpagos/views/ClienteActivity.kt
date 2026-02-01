package com.asperezg.gestionpagos.views

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.controllers.CartController
import com.google.android.material.card.MaterialCardView
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
        val cardCatalogo = findViewById<MaterialCardView>(R.id.cardCatalogo)
        val cardMisDeudas = findViewById<MaterialCardView>(R.id.cardMisDeudas)
        val fabCarrito = findViewById<FloatingActionButton>(R.id.fabCarrito)

        // Cargar perfil del usuario
        val uid = auth.currentUser?.uid ?: ""
        if (uid.isNotEmpty()) {
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

        // Navegación a Módulos
        cardCatalogo.setOnClickListener {
            startActivity(Intent(this, CatalogoProductosActivity::class.java))
        }

        cardMisDeudas.setOnClickListener {
            // Reemplazar con MisDeudasActivity cuando se cree
            Toast.makeText(this, "Módulo de Deudas en desarrollo", Toast.LENGTH_SHORT).show()
        }

        fabCarrito.setOnClickListener {
            if (CartController.obtenerItems().isNotEmpty()) {
                startActivity(Intent(this, CartActivity::class.java))
            } else {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }
}