package com.asperezg.gestionpagos.models

data class User(
    val id: String = "",
    val nombre: String = "",
    val cedula: String = "",
    val correo: String = "",
    val telefono: String = "",
    val rol: String = "cliente" ,// Por defecto siempre es cliente
    val tieneDeuda: Boolean = false // Bandera para validación de eliminación (D-15)
)