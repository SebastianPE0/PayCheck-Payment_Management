package com.asperezg.gestionpagos.views

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.controllers.AuthController
import com.asperezg.gestionpagos.models.User
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    // Instancia del controlador para manejar la lógica de negocio
    private val authController = AuthController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Referencias a los componentes del XML
        val etNombre = findViewById<TextInputEditText>(R.id.etNombre)
        val etCedula = findViewById<TextInputEditText>(R.id.etCedula)
        val etCorreo = findViewById<TextInputEditText>(R.id.etCorreo)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val cedula = etCedula.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            // Validaciones básicas antes de procesar
            if (nombre.isNotEmpty() && cedula.isNotEmpty() && correo.isNotEmpty() && pass.isNotEmpty()) {

                // Creamos el objeto User con los datos capturados
                // El ID se actualizará en el controlador tras la creación en Auth
                val nuevoUsuario = User(
                    nombre = nombre,
                    cedula = cedula,
                    correo = correo,
                    rol = "cliente" // Siempre cliente por defecto en este formulario
                )

                // Llamada al controlador para realizar el registro
                authController.registrarCliente(nuevoUsuario, pass) { exito, mensaje ->
                    if (exito) {
                        Toast.makeText(this, "Registro exitoso. Ya puedes iniciar sesión.", Toast.LENGTH_SHORT).show()
                        finish() // Cierra esta pantalla y vuelve al Login
                    } else {
                        Toast.makeText(this, "Error: $mensaje", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}