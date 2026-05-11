package com.example.tfg.data.api

import com.example.tfg.data.model.*
import retrofit2.http.*

interface ApiService {

    // ===== Usuarios =====
    @POST("usuarios/registro")
    suspend fun registro(
        @retrofit2.http.Header("X-Recaptcha-Token") recaptchaToken: String?,
        @Body body: UsuarioRegistro
    ): RegistroResponse

    @POST("usuarios/login")
    suspend fun login(@Body body: LoginRequest): String

    // ===== Auth público (recuperar contraseña) =====
    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequest): MessageResponse

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordRequest): MessageResponse

    @POST("auth/google")
    suspend fun loginGoogle(@Body body: GoogleLoginRequest): TokenResponse

    @PUT("usuarios/cambiar-contrasena")
    suspend fun cambiarContrasena(@Body body: CambiarContrasenaRequest): MessageResponse

    @PUT("usuarios/perfil")
    suspend fun actualizarPerfil(@Body body: UsuarioDTO): PerfilUpdateResponse

    @PUT("usuarios/preferencias")
    suspend fun actualizarPreferencias(@Body body: PreferenciasRequest): PreferenciasResponse

    // ===== Listas =====
    @GET("listas")
    suspend fun obtenerListas(): List<ListaDTO>

    @GET("listas/{id}")
    suspend fun obtenerLista(@Path("id") id: Long): ListaDTO

    @POST("listas")
    suspend fun crearLista(@Body body: CrearListaRequest): ListaDTO

    @PUT("listas/{id}")
    suspend fun renombrarLista(@Path("id") id: Long, @Body body: RenombrarListaRequest): ListaDTO

    @PUT("listas/{id}/config")
    suspend fun configurarLista(@Path("id") id: Long, @Body body: ConfigListaRequest): ListaDTO

    @DELETE("listas/{id}")
    suspend fun eliminarLista(@Path("id") id: Long): String

    @POST("listas/{id}/items")
    suspend fun anadirItem(@Path("id") id: Long, @Body body: ProductoListaDTO): ProductoListaDTO

    @POST("listas/{idDestino}/items/copiar")
    suspend fun copiarItems(@Path("idDestino") idDestino: Long, @Body ids: List<Long>): List<ProductoListaDTO>

    @PUT("listas/{id}/items/{productoId}")
    suspend fun editarItem(
        @Path("id") id: Long,
        @Path("productoId") productoId: Long,
        @Body body: ProductoListaDTO
    ): ProductoListaDTO

    @DELETE("listas/{id}/items/{productoId}")
    suspend fun eliminarItem(@Path("id") id: Long, @Path("productoId") productoId: Long): String

    @POST("listas/{idLista}/items/desde-favorito/{idFav}")
    suspend fun anadirDesdeFavorito(@Path("idLista") idLista: Long, @Path("idFav") idFav: Long): ProductoListaDTO

    // ===== Amistad =====
    @POST("amistad/solicitudes")
    suspend fun enviarSolicitud(@Body body: EnviarSolicitudRequest): SolicitudAmistadDTO

    @GET("amistad/solicitudes")
    suspend fun obtenerSolicitudes(): List<SolicitudAmistadDTO>

    @POST("amistad/solicitudes/{id}/aceptar")
    suspend fun aceptarSolicitud(@Path("id") id: Long): String

    @DELETE("amistad/solicitudes/{id}")
    suspend fun rechazarSolicitud(@Path("id") id: Long): String

    @GET("amistad")
    suspend fun obtenerAmigos(): List<AmigoDTO>

    @DELETE("amistad/{idAmigo}")
    suspend fun eliminarAmigo(@Path("idAmigo") idAmigo: Long): String

    // ===== Favoritos =====
    @GET("favoritos")
    suspend fun obtenerFavoritos(): List<FavoritoDTO>

    @POST("favoritos")
    suspend fun crearFavorito(@Body body: FavoritoDTO): FavoritoDTO

    @PUT("favoritos/{id}")
    suspend fun editarFavorito(@Path("id") id: Long, @Body body: FavoritoDTO): FavoritoDTO

    @DELETE("favoritos/{id}")
    suspend fun eliminarFavorito(@Path("id") id: Long): String

    // ===== Invitaciones =====
    @POST("invitaciones")
    suspend fun enviarInvitacion(@Body body: EnviarInvitacionRequest): InvitacionListaDTO

    @GET("invitaciones")
    suspend fun obtenerInvitaciones(): List<InvitacionListaDTO>

    @POST("invitaciones/{id}/aceptar")
    suspend fun aceptarInvitacion(@Path("id") id: Long): String

    @DELETE("invitaciones/{id}")
    suspend fun rechazarInvitacion(@Path("id") id: Long): String

    // ===== Admin =====
    @GET("api/admin/usuarios")
    suspend fun adminListarUsuarios(@Query("q") q: String? = null): List<AdminUsuarioDTO>

    @PUT("api/admin/usuarios/{id}/rol")
    suspend fun adminCambiarRol(@Path("id") id: Long, @Body body: CambiarRolRequest): CambiarRolResponse

    @DELETE("api/admin/usuarios/{id}")
    suspend fun adminEliminarUsuario(@Path("id") id: Long): MessageResponse

    // ===== Presets =====
    @GET("unaVez/obtenerSupermercadosPorDefecto")
    suspend fun obtenerSupermercadosDefecto(): List<String>

    @GET("unaVez/obtenerProductosPorDefecto")
    suspend fun obtenerProductosDefecto(): List<ProductoCatalogoDTO>
}
