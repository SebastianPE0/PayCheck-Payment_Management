package com.asperezg.gestionpagos.views

import android.os.Bundle
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
        adapter = UserAdapter(listaClientes)
        rvClientes.adapter = adapter

        consultarClientes()
    }

    private fun consultarClientes() {
        // Consultamos solo los que tienen rol cliente
        db.collection("Usuarios")
            .whereEqualTo("rol", "cliente")
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener

                listaClientes.clear()
                if (value != null) {
                    for (doc in value) {
                        val usuario = doc.toObject(User::class.java)
                        if (usuario != null) listaClientes.add(usuario)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }
}