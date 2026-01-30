package com.asperezg.gestionpagos.controllers
import com.asperezg.gestionpagos.models.CartItem

object CartController {
    private val items = mutableListOf<CartItem>()

    fun agregarProducto(nuevoItem: CartItem) {
        // Si el producto ya está, solo sumamos la cantidad
        val existente = items.find { it.productoId == nuevoItem.productoId }
        if (existente != null) {
            existente.cantidad += nuevoItem.cantidad
        } else {
            items.add(nuevoItem)
        }
    }

    fun obtenerItems(): List<CartItem> = items

    fun obtenerTotal(): Double = items.sumOf { it.precio * it.cantidad }

    fun limpiarCarrito() {
        items.clear()
    }
}
