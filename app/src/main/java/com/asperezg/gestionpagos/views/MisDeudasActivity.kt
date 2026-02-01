package com.asperezg.gestionpagos.views

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.adapters.CuotaAdapter
import com.asperezg.gestionpagos.models.Cuota
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.util.*

class MisDeudasActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var adapter: CuotaAdapter

    private var imagenSeleccionadaUri: Uri? = null
    private var previewImagen: ImageView? = null
    private val mesesArray = arrayOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imagenSeleccionadaUri = uri
            previewImagen?.setImageURI(uri)
            previewImagen?.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_deudas)

        // Configuración RecyclerView
        val rv = findViewById<RecyclerView>(R.id.rvCuotas)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = CuotaAdapter(listOf()) { cuota -> mostrarDialogoPago(cuota) }
        rv.adapter = adapter

        // Configuración del Spinner de Meses
        val spinner = findViewById<Spinner>(R.id.spinnerMeses)
        val adapterMeses = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mesesArray)
        spinner.adapter = adapterMeses

        val mesActual = Calendar.getInstance().get(Calendar.MONTH)
        spinner.setSelection(mesActual)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                consultarMisDeudas(position) // Filtra según el mes seleccionado
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun consultarMisDeudas(mesSeleccionado: Int) {
        val uid = auth.currentUser?.uid ?: return

        // Cálculo del rango de tiempo del mes
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, mesSeleccionado)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        val inicioMes = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        val finMes = calendar.timeInMillis

        // Consulta filtrada por cliente y rango de fecha
        db.collection("Deudas")
            .whereEqualTo("idCliente", uid)
            .whereGreaterThanOrEqualTo("fechaVencimiento", inicioMes)
            .whereLessThanOrEqualTo("fechaVencimiento", finMes)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    val cuotas = value.toObjects(Cuota::class.java).sortedBy { it.fechaVencimiento }
                    adapter.actualizar(cuotas)
                    findViewById<TextView>(R.id.tvResumenDeuda).text =
                        "En ${mesesArray[mesSeleccionado]} tienes ${cuotas.count { it.estado == "pendiente" }} cuotas pendientes"
                }
            }
    }

    private fun mostrarDialogoPago(cuota: Cuota) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_registrar_pago, null)

        val etComprobante = view.findViewById<EditText>(R.id.etNumeroComprobante)
        val btnFoto = view.findViewById<Button>(R.id.btnSubirFoto)
        val btnEnviar = view.findViewById<Button>(R.id.btnEnviarPago)
        previewImagen = view.findViewById(R.id.ivPreviewComprobante)

        imagenSeleccionadaUri = null
        btnFoto.setOnClickListener { galleryLauncher.launch("image/*") }

        builder.setView(view)
        val dialog = builder.create()

        btnEnviar.setOnClickListener {
            val num = etComprobante.text.toString()
            if (num.isNotEmpty() && imagenSeleccionadaUri != null) {
                btnEnviar.isEnabled = false
                btnEnviar.text = "Procesando..."
                procesarPagoBase64(cuota, num, dialog)
            } else {
                Toast.makeText(this, "Falta el número o la foto", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun procesarPagoBase64(cuota: Cuota, numRef: String, dialog: AlertDialog) {
        val cuotaId = "${cuota.idSolicitud}_cuota_${cuota.numeroCuota}"
        try {
            val inputStream = contentResolver.openInputStream(imagenSeleccionadaUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream)
            val bytes = outputStream.toByteArray()
            val base64String = Base64.encodeToString(bytes, Base64.DEFAULT)

            val update = mapOf(
                "estado" to "revision",
                "referenciaPago" to numRef,
                "comprobanteImagen" to base64String, // Imagen guardada como texto Base64
                "fechaNotificacion" to System.currentTimeMillis()
            )

            db.collection("Deudas").document(cuotaId).update(update)
                .addOnSuccessListener {
                    Toast.makeText(this, "Notificación enviada con éxito", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al procesar imagen", Toast.LENGTH_SHORT).show()
        }
    }
}