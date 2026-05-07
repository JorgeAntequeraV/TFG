package com.example.tfg.ui.nav

object Routes {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val LISTAS = "listas"
    const val DENTRO_LISTA = "lista/{id}"
    const val FAVORITOS = "favoritos"
    const val ADD_FAVORITO = "favoritos/add"
    const val PERFIL = "perfil"
    const val CAMBIAR_CORREO = "perfil/cambiar-correo"
    const val CAMBIAR_CONTRASENA = "perfil/cambiar-contrasena"
    const val ADD_ITEM_LISTA = "lista/{id}/add"
    const val ADMIN = "admin"
    const val NOTIFICACIONES = "notificaciones"
    const val AMIGOS = "amigos"

    fun dentroLista(id: Long) = "lista/$id"
    fun addItemLista(id: Long) = "lista/$id/add"
}
