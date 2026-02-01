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

        // 1. Gestión de Clientes: Registro y visualización de usuarios
        findViewById<MaterialCardView>(R.id.cardGestionClientes).setOnClickListener {
            startActivity(Intent(this, GestionClientesActivity::class.java))
        }

        // 2. Gestión de Productos: Inventario y stock
        findViewById<MaterialCardView>(R.id.cardGestionProductos).setOnClickListener {
            startActivity(Intent(this, GestionProductosActivity::class.java))
        }

        // 3. Aprobación de Compras: Revisar solicitudes y generar cuotas automáticamente
        findViewById<MaterialCardView>(R.id.cardAprobaciones).setOnClickListener {
            startActivity(Intent(this, AprobacionActivity::class.java))
        }

        // 4. Módulo de Deudores: Listado general para cobranzas
        findViewById<MaterialCardView>(R.id.cardDeudores).setOnClickListener {
            // Módulo para ver quién debe y cuánto le falta
            Toast.makeText(this, "Módulo de Deudores en desarrollo", Toast.LENGTH_SHORT).show()
        }

        // 5. Validar Pagos: Revisar fotos Base64 enviadas desde el Infinix
        findViewById<MaterialCardView>(R.id.cardPagos).setOnClickListener {
            // Este es el módulo que acabamos de terminar para validar comprobantes
            startActivity(Intent(this, ValidarPagosActivity::class.java))
        }
    }
}