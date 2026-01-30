package com.asperezg.gestionpagos.views

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.controllers.CartController

class CartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val rvCarrito = findViewById<RecyclerView>(R.id.rvCarrito)
        val tvTotal = findViewById<TextView>(R.id.tvTotalCarrito)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmarCredito)

        tvTotal.text = "Total: $${CartController.obtenerTotal()}"

        // Configuración básica del RecyclerView para ver los items del carrito
        rvCarrito.layoutManager = LinearLayoutManager(this)

        btnConfirmar.setOnClickListener {
            // Aquí llamarás a SaleRepository para procesar la venta (Próximo paso)
        }
    }
}