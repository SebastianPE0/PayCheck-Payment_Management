package com.asperezg.gestionpagos.models

data class CartItem(
    val productoId: String,
    val nombre: String,
    val precio: Double,
    var cantidad: Int = 1
)