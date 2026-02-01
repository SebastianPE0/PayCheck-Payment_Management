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
                    // Log para ver cuántos documentos encontró en bruto
                    android.util.Log.d("FIREBASE_DEBUG", "Documentos encontrados: ${value.size()}")

                    try {
                        val nuevaLista = value.toObjects(Solicitud::class.java)
                        // Log para ver cuántos objetos se convirtieron bien
                        android.util.Log.d("FIREBASE_DEBUG", "Objetos convertidos: ${nuevaLista.size}")
                        adapter.actualizarLista(nuevaLista)
                    } catch (e: Exception) {
                        android.util.Log.e("FIREBASE_DEBUG", "Error de conversión: ${e.message}")
                        e.printStackTrace() // Esto te dará el detalle exacto del error en el Logcat
                    }
                }
            }
    }

    private fun gestionarSolicitud(s: Solicitud, nuevoEstado: String) {
        if (nuevoEstado == "rechazada") {
            devolverStock(s) // Devolvemos lo reservado
        }

        db.collection("Solicitudes").document(s.id).update("estado", nuevoEstado)
            .addOnSuccessListener {
                if (nuevoEstado == "aprobada") {
                    db.collection("Usuarios").document(s.idCliente).update("tieneDeuda", true)
                    Toast.makeText(this, "Venta confirmada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Solicitud rechazada y stock devuelto", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun devolverStock(s: Solicitud) {
        val batch = db.batch()
        s.productos.forEach { item ->
            val productoRef = db.collection("Productos").document(item.producto.id)
            // Usamos FieldValue.increment para ser precisos con la suma
            batch.update(productoRef, "stock", com.google.firebase.firestore.FieldValue.increment(item.cantidad.toLong()))
        }
        batch.commit()
    }
}