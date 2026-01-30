package com.asperezg.gestionpagos.controllers


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.asperezg.gestionpagos.models.User

class AuthController {
    // Instancias de Firebase necesarias para autenticación y base de datos
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Registra un nuevo cliente en Firebase Auth y guarda su perfil en Firestore.
     */
    fun registrarCliente(userModel: User, contrasena: String, onResult: (Boolean, String?) -> Unit) {
        // 1. Crear el usuario en Firebase Authentication
        auth.createUserWithEmailAndPassword(userModel.correo, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Si se creó con éxito, obtenemos el UID generado por Firebase
                    val uid = auth.currentUser?.uid ?: ""

                    // Actualizamos el modelo con el ID real de Firebase
                    val userFinal = userModel.copy(id = uid)

                    // 2. Guardar los datos adicionales en la colección "Usuarios" de Firestore
                    db.collection("Usuarios").document(uid).set(userFinal)
                        .addOnSuccessListener {
                            onResult(true, "Registro exitoso")
                        }
                        .addOnFailureListener { e ->
                            onResult(false, "Error al guardar perfil: ${e.message}")
                        }
                } else {
                    onResult(false, "Error de autenticación: ${task.exception?.message}")
                }
            }
    }
    fun iniciarSesion(correo: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(correo, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Acceso concedido")
                } else {
                    onResult(false, "Error: ${task.exception?.message}")
                }
            }
    }
    // Dentro de AuthController.kt
    fun obtenerRolYRedireccionar(uid: String, onResult: (String?) -> Unit) {
        // Accedemos a la colección de Usuarios usando el UID único
        db.collection("Usuarios").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val rol = document.getString("rol") // Extraemos el campo "rol"
                    onResult(rol)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { onResult(null) }
    }
}