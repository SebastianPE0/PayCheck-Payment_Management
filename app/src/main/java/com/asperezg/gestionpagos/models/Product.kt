package com.asperezg.gestionpagos.models

data class Product(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precioContado: Double = 0.0,
    val stock: Int = 0,
    val imageUrl: String = ""
)