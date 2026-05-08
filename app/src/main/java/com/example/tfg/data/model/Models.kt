package com.example.tfg.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UsuarioRegistro(
    val nombre: String,
    val email: String,
    val nombreUsuario: String,
    val contrasena: String,
    val telefono: String? = null
)

@JsonClass(generateAdapter = true)
data class RegistroResponse(
    val message: String?,
    val userId: Long?
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val nombreUsuario: String,
    val contrasena: String
)

@JsonClass(generateAdapter = true)
data class CambiarContrasenaRequest(
    val contrasenaActual: String,
    val nuevaContrasena: String
)

@JsonClass(generateAdapter = true)
data class ForgotPasswordRequest(
    val nombreUsuario: String
)

@JsonClass(generateAdapter = true)
data class ResetPasswordRequest(
    val token: String,
    val nuevaContrasena: String
)

@JsonClass(generateAdapter = true)
data class UsuarioDTO(
    val id: Long? = null,
    val nombre: String? = null,
    val email: String? = null,
    val nombreUsuario: String? = null,
    val tagAmigo: String? = null,
    val rol: String? = null,
    val telefono: String? = null
)

@JsonClass(generateAdapter = true)
data class PerfilUpdateResponse(
    val message: String?,
    val usuario: UsuarioDTO?
)

@JsonClass(generateAdapter = true)
data class PreferenciasRequest(
    val temaOscuro: Boolean
)

@JsonClass(generateAdapter = true)
data class PreferenciasResponse(
    val temaOscuro: Boolean
)

@JsonClass(generateAdapter = true)
data class ProductoListaDTO(
    val id: Long? = null,
    val nombre: String,
    val cantidad: String? = null,
    val unidadMedida: String? = null,
    val precio: Double? = null
)

@JsonClass(generateAdapter = true)
data class ListaDTO(
    val id: Long? = null,
    val nombre: String,
    val supermercado: String? = null,
    val tagAmigoCreador: String? = null,
    val ordenAscendente: Boolean? = null,
    val mostrarPrecios: Boolean? = null,
    val productos: List<ProductoListaDTO>? = null,
    val total: Double? = null
)

@JsonClass(generateAdapter = true)
data class CrearListaRequest(
    val nombre: String,
    val supermercado: String? = null
)

@JsonClass(generateAdapter = true)
data class RenombrarListaRequest(
    val nombre: String
)

@JsonClass(generateAdapter = true)
data class ConfigListaRequest(
    val ordenAscendente: Boolean,
    val mostrarPrecios: Boolean
)

@JsonClass(generateAdapter = true)
data class SolicitudAmistadDTO(
    val id: Long,
    val tagRemitente: String?,
    val nombreRemitente: String?,
    val fechaCreacion: String?
)

@JsonClass(generateAdapter = true)
data class EnviarSolicitudRequest(
    val tagDestinatario: String
)

@JsonClass(generateAdapter = true)
data class AmigoDTO(
    val id: Long,
    val nombreUsuario: String?,
    val tagAmigo: String?
)

@JsonClass(generateAdapter = true)
data class FavoritoDTO(
    val id: Long? = null,
    val nombre: String,
    val cantidad: String? = null,
    val unidadMedida: String? = null,
    val precio: Double? = null
)

@JsonClass(generateAdapter = true)
data class InvitacionListaDTO(
    val id: Long,
    val listaId: Long?,
    val nombreLista: String?,
    val nombreAnfitrion: String?,
    val fechaInvitacion: String?
)

@JsonClass(generateAdapter = true)
data class EnviarInvitacionRequest(
    val idLista: Long,
    val tagInvitado: String
)

@JsonClass(generateAdapter = true)
data class AdminUsuarioDTO(
    val id: Long,
    val nombreUsuario: String?,
    val email: String?,
    val tagAmigo: String?,
    val rol: String?
)

@JsonClass(generateAdapter = true)
data class CambiarRolRequest(
    val rol: String
)

@JsonClass(generateAdapter = true)
data class CambiarRolResponse(
    val id: Long,
    val rol: String
)

@JsonClass(generateAdapter = true)
data class MessageResponse(
    val message: String?
)

@JsonClass(generateAdapter = true)
data class ProductoCatalogoDTO(
    val id: Long?,
    val nombre: String,
    val cantidad: String? = null,
    val precio: Double? = null,
    val categoria: String? = null
)
