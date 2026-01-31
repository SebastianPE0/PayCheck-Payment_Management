package com.asperezg.gestionpagos.models
data class Solicitud(
    val id: String = "",
    val idCliente: String = "",
    val nombreCliente: String = "",
    val productos: List<CartItem> = listOf(),
    val total: Double = 0.0,
    val estado: String = "pendiente", // pendiente, aprobada, rechazada
    val fecha: Long = System.currentTimeMillis()
)
