package com.asperezg.gestionpagos.views

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.adapters.UserAdapter
import com.asperezg.gestionpagos.models.User
import com.google.firebase.firestore.FirebaseFirestore

class GestionClientesActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var rvClientes: RecyclerView
    private lateinit var adapter: UserAdapter
    private val listaClientes = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_clientes)

        rvClientes = findViewById(R.id.rvClientesAdmin)
        rvClientes.layoutManager = LinearLayoutManager(this)

        // Inicialización del adaptador con la función de clic
        adapter = UserAdapter(listaClientes) { usuarioSeleccionado ->
            mostrarDialogoEditar(usuarioSeleccionado)
        }
        rvClientes.adapter = adapter

        consultarClientes()
    }

    private fun mostrarDialogoEditar(u: User) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val vista = layoutInflater.inflate(R.layout.dialog_editar_usuario, null)

        val btnEliminar = vista.findViewById<Button>(R.id.btnEliminarCliente)
        val etNombre = vista.findViewById<EditText>(R.id.etNombreEdit)
        val etCorreo = vista.findViewById<EditText>(R.id.etCorreoEdit)
        val etTelefono = vista.findViewById<EditText>(R.id.etTelefonoEdit)
        val spRol = vista.findViewById<Spinner>(R.id.spRolEdit)
        val swDeuda = vista.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.swDeudaEdit)

        val roles = arrayOf("cliente", "admin")
        val adapterRol = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        spRol.adapter = adapterRol

        etNombre.setText(u.nombre)
        etCorreo.setText(u.correo)
        etTelefono.setText(u.telefono)
        swDeuda.isChecked = u.tieneDeuda
        val posicion = roles.indexOf(u.rol)
        if (posicion >= 0) spRol.setSelection(posicion)

        builder.setView(vista)

        // Configuración de botones del Builder
        builder.setPositiveButton("Actualizar") { _, _ ->
            val nuevoNombre = etNombre.text.toString()
            val nuevoCorreo = etCorreo.text.toString()
            val nuevoTelefono = etTelefono.text.toString()
            val nuevoRol = spRol.selectedItem.toString()
            val nuevaDeuda = swDeuda.isChecked

            db.collection("Usuarios").document(u.id)
                .update(mapOf(
                    "nombre" to nuevoNombre,
                    "correo" to nuevoCorreo,
                    "telefono" to nuevoTelefono,
                    "rol" to nuevoRol,
                    "tieneDeuda" to nuevaDeuda
                ))
                .addOnSuccessListener {
                    Toast.makeText(this, "Cliente actualizado", Toast.LENGTH_SHORT).show()
                }
        }
        builder.setNegativeButton("Cancelar", null)

        // IMPORTANTE: Creamos el diálogo y lo mostramos una sola vez
        val dialogActual = builder.create()

        btnEliminar.setOnClickListener {
            if (u.tieneDeuda) {
                Toast.makeText(this, "No se puede eliminar un cliente con deuda activa", Toast.LENGTH_LONG).show()
            } else {
                confirmarEliminacion(u, dialogActual) // Pasamos la referencia correcta para cerrarlo
            }
        }

        dialogActual.show()
    }

    private fun confirmarEliminacion(u: User, editDialog: androidx.appcompat.app.AlertDialog) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("¿Eliminar cliente?")
            .setMessage("Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                db.collection("Usuarios").document(u.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show()
                        editDialog.dismiss() // Ahora sí cerrará la ventana de edición
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun consultarClientes() {
        db.collection("Usuarios")
            .whereEqualTo("rol", "cliente")
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener

                val listaNueva = mutableListOf<User>()
                if (value != null) {
                    for (doc in value) {
                        val usuario = doc.toObject(User::class.java)
                        if (usuario != null) listaNueva.add(usuario)
                    }
                    // Usamos la función del adaptador para evitar duplicados
                    adapter.actualizarLista(listaNueva)
                }
            }
    }
}