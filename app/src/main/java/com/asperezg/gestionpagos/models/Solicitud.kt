package com.asperezg.gestionpagos.models

data class Solicitud(
    val id: String = "",
    val idCliente: String = "",
    val nombreCliente: String = "",
    val correoCliente: String = "", // Este campo es vital según tu imagen
    val productos: List<CartItem> = listOf(),
    val total: Double = 0.0,
    val estado: String = "pendiente",
    val fecha: Long = 0L,
    val numeroCuotas: Int = 1, // Por defecto 1 (contado)
    val montoCuota: Double = 0.0

)