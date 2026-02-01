package com.asperezg.gestionpagos.views

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.adapters.AprobacionAdapter
import com.asperezg.gestionpagos.models.Solicitud
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AprobacionActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: AprobacionAdapter
    private val listaSolicitudes = mutableListOf<Solicitud>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aprobacion)

        val rv = findViewById<RecyclerView>(R.id.rvSolicitudesPendientes)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = AprobacionAdapter(listaSolicitudes,
            { sol -> gestionarSolicitud(sol, "aprobada") },
            { sol -> gestionarSolicitud(sol, "rechazada") }
        )
        rv.adapter = adapter

        escucharSolicitudes()
    }

    private fun escucharSolicitudes() {
        db.collection("Solicitudes")
            .whereEqualTo("estado", "pendiente")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    android.util.Log.e("FIREBASE_DEBUG", "Error de Firebase: ${error.message}")
                    return@addSnapshotListener
                }

                if (value != null) {
                    try {
                        val nuevaLista = value.toObjects(Solicitud::class.java)
                        adapter.actualizarLista(nuevaLista)
                    } catch (e: Exception) {
                        android.util.Log.e("FIREBASE_DEBUG", "Error de conversión: ${e.message}")
                    }
                }
            }
    }

    private fun gestionarSolicitud(s: Solicitud, nuevoEstado: String) {
        if (nuevoEstado == "rechazada") {
            devolverStock(s) // Si se rechaza, el stock vuelve al inventario
        }

        db.collection("Solicitudes").document(s.id).update("estado", nuevoEstado)
            .addOnSuccessListener {
                if (nuevoEstado == "aprobada") {
                    // Acción clave: Crear el plan de pagos antes de finalizar
                    generarPlanDePagos(s)
                    db.collection("Usuarios").document(s.idCliente).update("tieneDeuda", true)
                    Toast.makeText(this, "Venta aprobada y plan de cuotas generado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Solicitud rechazada y stock devuelto", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun generarPlanDePagos(s: Solicitud) {
        val batch = db.batch()
        val calendario = Calendar.getInstance()

        // Creamos cada cuota según el plazo que el cliente eligió
        for (i in 1..s.numeroCuotas) {
            // Programamos los vencimientos cada 30 días
            calendario.add(Calendar.DAY_OF_YEAR, 30)

            val cuotaId = "${s.id}_cuota_$i"
            val cuotaRef = db.collection("Deudas").document(cuotaId)

            val datosCuota = hashMapOf(
                "idSolicitud" to s.id,
                "idCliente" to s.idCliente,
                "nombreCliente" to s.nombreCliente,
                "numeroCuota" to i,
                "totalCuotas" to s.numeroCuotas,
                "monto" to s.montoCuota,
                "fechaVencimiento" to calendario.timeInMillis,
                "estado" to "pendiente"
            )

            batch.set(cuotaRef, datosCuota)
        }

        batch.commit().addOnFailureListener {
            android.util.Log.e("ERROR_PLAN", "Fallo al crear cuotas: ${it.message}")
        }
    }

    private fun devolverStock(s: Solicitud) {
        val batch = db.batch()
        s.productos.forEach { item ->
            val productoRef = db.collection("Productos").document(item.producto.id)
            // FieldValue.increment devuelve el stock reservado al catálogo
            batch.update(productoRef, "stock", com.google.firebase.firestore.FieldValue.increment(item.cantidad.toLong()))
        }
        batch.commit()
    }
}