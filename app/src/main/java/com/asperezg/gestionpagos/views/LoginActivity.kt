package com.asperezg.gestionpagos.views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.asperezg.gestionpagos.R
import com.asperezg.gestionpagos.controllers.AuthController
import com.asperezg.gestionpagos.views.ClienteActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    // Instancia del controlador para manejar la lógica de negocio
    private val authController = AuthController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Referencias a los componentes de la vista (XML)
        val etCorreo = findViewById<TextInputEditText>(R.id.etLoginCorreo)
        val etPass = findViewById<TextInputEditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvIrARegistro = findViewById<TextView>(R.id.tvIrARegistro)

        // Acción del botón de Inicio de Sesión
        btnLogin.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val pass = etPass.text.toString().trim()

            // Validación local de campos vacíos
            if (correo.isNotEmpty() && pass.isNotEmpty()) {
                authController.iniciarSesion(correo, pass) { exito, mensaje ->
                    if (exito) {
                        // Una vez autenticado, verificamos el ROL del usuario (D-03)
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        uid?.let { idUsuario ->
                            verificarRolYRedireccionar(idUsuario)
                        }
                    } else {
                        // Mostramos el error devuelto por Firebase
                        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        // Navegación hacia la pantalla de registro
        tvIrARegistro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Función privada para gestionar la navegación según el tipo de usuario
     */
    private fun verificarRolYRedireccionar(uid: String) {
        authController.obtenerRolYRedireccionar(uid) { rol ->
            when (rol) {
                "admin" -> {
                    // Si es Admin, va al panel de gestión de productos e inventario
                    // TODO: Crear AdminActivity
                    startActivity(Intent(this, AdminActivity::class.java))
                    Toast.makeText(this, "Sesión como Administrador", Toast.LENGTH_SHORT).show()
                    finish() // Cerramos el Login para que no pueda volver atrás con el botón físico
                }
                "cliente" -> {
                    // Si es Cliente, va a su panel de consulta de cuotas (D-20)
                    // TODO: Crear ClienteActivity
                    startActivity(Intent(this, ClienteActivity::class.java))
                    Toast.makeText(this, "Sesión como Cliente", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {
                    Toast.makeText(this, "Error: No se encontró el rol del usuario", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}