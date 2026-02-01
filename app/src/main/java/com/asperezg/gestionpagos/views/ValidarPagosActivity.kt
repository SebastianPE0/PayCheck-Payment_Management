package com.asperezg.gestionpagos.views

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.adapters.ValidacionPagoAdapter
import com.asperezg.gestionpagos.models.Cuota
import com.google.firebase.firestore.FirebaseFirestore

class ValidarPagosActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: ValidacionPagoAdapter // Usamos el adaptador específico del Admin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validar_pagos)

        val rv = findViewById<RecyclerView>(R.id.rvPagosRevision)
        rv.layoutManager = LinearLayoutManager(this)

        // Inicializamos con el adaptador que SI permite clics en estado 'revision'
        adapter = ValidacionPagoAdapter(listOf()) { cuota ->
            mostrarDetallePago(cuota)
        }
        rv.adapter = adapter

        escucharPagosEnRevision()
    }

    private fun escucharPagosEnRevision() {
        // Escucha global de todas las cuotas que esperan validación
        db.collection("Deudas")
            .whereEqualTo("estado", "revision")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("FIRESTORE_ERROR", error.message.toString())
                    return@addSnapshotListener
                }
                if (value != null) {
                    val lista = value.toObjects(Cuota::class.java)
                    adapter.actualizar(lista)
                }
            }
    }

    private fun mostrarDetallePago(cuota: Cuota) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_ver_comprobante, null)

        val ivComprobante = view.findViewById<ImageView>(R.id.ivComprobanteAdmin)
        val tvRef = view.findViewById<TextView>(R.id.tvReferenciaAdmin)
        val btnAprobar = view.findViewById<Button>(R.id.btnAprobarPago)
        val btnRechazar = view.findViewById<Button>(R.id.btnRechazarPago)

        tvRef.text = "Referencia: ${cuota.referenciaPago}"

        // Decodificación segura de la imagen Base64 enviada desde el Infinix
        if (cuota.comprobanteImagen.isNotEmpty()) {
            try {
                val imageBytes = Base64.decode(cuota.comprobanteImagen, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ivComprobante.setImageBitmap(decodedImage)
            } catch (e: Exception) {
                ivComprobante.setImageResource(android.R.drawable.ic_menu_report_image)
                Toast.makeText(this, "Error al decodificar imagen", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setView(view)
        val dialog = builder.create()

        btnAprobar.setOnClickListener {
            actualizarEstadoCuota(cuota, "pagada", dialog)
        }

        btnRechazar.setOnClickListener {
            actualizarEstadoCuota(cuota, "pendiente", dialog)
        }

        dialog.show()
    }

    private fun actualizarEstadoCuota(cuota: Cuota, nuevoEstado: String, dialog: AlertDialog) {
        val docId = "${cuota.idSolicitud}_cuota_${cuota.numeroCuota}"

        // Al aprobar o rechazar, limpiamos los datos de notificación para permitir re-envío si falla
        val actualizaciones = mutableMapOf<String, Any>("estado" to nuevoEstado)

        if (nuevoEstado == "pendiente") {
            actualizaciones["comprobanteImagen"] = ""
            actualizaciones["referenciaPago"] = ""
        }

        db.collection("Deudas").document(docId).update(actualizaciones)
            .addOnSuccessListener {
                Toast.makeText(this, "Pago ${if (nuevoEstado == "pagada") "Aprobado" else "Rechazado"}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}