package com.asperezg.gestionpagos.models

data class Cuota(
    val idSolicitud: String = "",
    val idCliente: String = "",
    val numeroCuota: Int = 0,
    val totalCuotas: Int = 0,
    val monto: Double = 0.0,
    val fechaVencimiento: Long = 0L,
    val estado: String = "pendiente",
    // CAMPOS NUEVOS PARA EL PAGO:
    val fechaNotificacion: Long = 0L,
    val referenciaPago: String = "",
    val comprobanteImagen: String = "" // Aquí se guarda el Base64
)