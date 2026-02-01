package com.asperezg.gestionpagos.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asperezg.gestionpagos.R
import com.google.android.material.card.MaterialCardView

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // 1. Gestión de Clientes
        val cardClientes = findViewById<MaterialCardView>(R.id.cardGestionClientes)
        cardClientes.setOnClickListener {
            startActivity(Intent(this, GestionClientesActivity::class.java))
        }

        // 2. Gestión de Productos
        val cardProductos = findViewById<MaterialCardView>(R.id.cardGestionProductos)
        cardProductos.setOnClickListener {
            startActivity(Intent(this, GestionProductosActivity::class.java))
        }

        // 3. NUEVO: Aprobación de Compras (Solicitudes pendientes)
        val cardAprobaciones = findViewById<MaterialCardView>(R.id.cardAprobaciones)
        cardAprobaciones.setOnClickListener {
            startActivity(Intent(this, AprobacionActivity::class.java))
        }

        // 4. Módulo de Deudores (Listado de deudas activas)
        val cardDeudores = findViewById<MaterialCardView>(R.id.cardDeudores)
        cardDeudores.setOnClickListener {
            // Este módulo lo crearemos a continuación
            //startActivity(Intent(this, DeudoresActivity::class.java))
            Toast.makeText(this, "Módulo de Deudores en desarrollo", Toast.LENGTH_SHORT).show()
        }

        // 5. Historial de Pagos
        val cardPagos = findViewById<MaterialCardView>(R.id.cardPagos)
        cardPagos.setOnClickListener {
            // Provisionalmente al mismo de deudores o un Toast
            Toast.makeText(this, "Módulo de Pagos en desarrollo", Toast.LENGTH_SHORT).show()
            //startActivity(Intent(this, PagosActivity::class.java))
        }
    }
}