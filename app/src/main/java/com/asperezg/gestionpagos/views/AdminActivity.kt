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
        // Esta línea es la que "prende" la vista
        setContentView(R.layout.activity_admin)

        // 1. Vincular la tarjeta de Productos
        val cardProductos = findViewById<MaterialCardView>(R.id.cardGestionProductos)
        cardProductos.setOnClickListener {
            val intent = Intent(this, GestionProductosActivity::class.java)
            startActivity(intent)
        }

        // 2. Vincular la tarjeta de Clientes (Provisional)
        val cardClientes = findViewById<MaterialCardView>(R.id.cardGestionClientes)
        cardClientes.setOnClickListener {
            Toast.makeText(this, "Módulo de Clientes en desarrollo", Toast.LENGTH_SHORT).show()
        }
    }
}