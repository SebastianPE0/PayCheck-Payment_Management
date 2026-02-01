package com.asperezg.gestionpagos.controllers

import com.asperezg.gestionpagos.models.CartItem
import com.asperezg.gestionpagos.models.Product

object CartController {
    private val carrito = mutableListOf<CartItem>()

    fun agregarAlCarrito(producto: Product) {
        val itemExistente = carrito.find { it.producto.id == producto.id }
        if (itemExistente != null) {
            itemExistente.cantidad++
        } else {
            carrito.add(CartItem(producto, 1))
        }
    }

    // ESTA ES LA FUNCIÓN QUE FALTABA
    fun eliminarDelCarrito(idProducto: String) {
        val item = carrito.find { it.producto.id == idProducto }
        if (item != null) {
            carrito.remove(item)
        }
    }

    fun obtenerItems(): List<CartItem> = carrito

    fun obtenerTotal(): Double {
        return carrito.sumOf { it.producto.precioContado * it.cantidad }
    }

    fun limpiarCarrito() {
        carrito.clear()
    }
}