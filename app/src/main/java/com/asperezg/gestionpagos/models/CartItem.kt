package com.asperezg.gestionpagos.models

data class CartItem(
    val producto: Product = Product(), // Debe tener un valor por defecto
    var cantidad: Int = 0
)