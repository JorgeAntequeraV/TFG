package com.example.tfg.data.repository

import com.example.tfg.data.api.ApiService
import com.example.tfg.data.local.PresetsCache
import com.example.tfg.data.model.*
import com.example.tfg.data.session.SessionManager
import com.example.tfg.data.util.JwtUtil
import retrofit2.HttpException

class Repository(
    private val api: ApiService,
    private val session: SessionManager,
    private val presets: PresetsCache
) {

    suspend fun login(usuario: String, contrasena: String): Result<String> = runCatching {
        val token = api.login(LoginRequest(usuario, contrasena)).trim('"')
        val claims = JwtUtil.parse(token)
        session.saveSession(token, claims?.userId, claims?.sub, claims?.role)
        token
    }.recoverCatching { throw mapError(it) }

    suspend fun loginConGoogle(idToken: String): Result<String> = runCatching {
        val response = api.loginGoogle(GoogleLoginRequest(idToken))
        val token = response.token.trim('"')
        val claims = JwtUtil.parse(token)
        session.saveSession(token, claims?.userId, claims?.sub, claims?.role)
        token
    }.recoverCatching { throw mapError(it) }

    suspend fun registro(req: UsuarioRegistro, recaptchaToken: String? = null): Result<RegistroResponse> = runCatching {
        api.registro(recaptchaToken, req)
    }.recoverCatching { throw mapError(it) }

    suspend fun logout() {
        session.clear()
    }

    // ===== Auth público — recuperación de contraseña =====
    suspend fun forgotPassword(nombreUsuario: String) = safe { api.forgotPassword(ForgotPasswordRequest(nombreUsuario)) }
    suspend fun resetPassword(token: String, nuevaContrasena: String) = safe { api.resetPassword(ResetPasswordRequest(token, nuevaContrasena)) }

    // listas
    suspend fun obtenerListas() = safe { api.obtenerListas() }
    suspend fun obtenerLista(id: Long) = safe { api.obtenerLista(id) }
    suspend fun crearLista(nombre: String, supermercado: String?) = safe { api.crearLista(CrearListaRequest(nombre, supermercado)) }
    suspend fun renombrarLista(id: Long, nombre: String) = safe { api.renombrarLista(id, RenombrarListaRequest(nombre)) }
    suspend fun configurarLista(id: Long, asc: Boolean, mostrarPrecios: Boolean) = safe { api.configurarLista(id, ConfigListaRequest(asc, mostrarPrecios)) }
    suspend fun eliminarLista(id: Long) = safe { api.eliminarLista(id) }
    suspend fun anadirItem(id: Long, p: ProductoListaDTO) = safe { api.anadirItem(id, p) }
    suspend fun copiarItems(idDestino: Long, ids: List<Long>) = safe { api.copiarItems(idDestino, ids) }
    suspend fun editarItem(idLista: Long, idProd: Long, p: ProductoListaDTO) = safe { api.editarItem(idLista, idProd, p) }
    suspend fun eliminarItem(idLista: Long, idProd: Long) = safe { api.eliminarItem(idLista, idProd) }
    suspend fun anadirDesdeFavorito(idLista: Long, idFav: Long) = safe { api.anadirDesdeFavorito(idLista, idFav) }

    // amistad
    suspend fun enviarSolicitud(tag: String) = safe { api.enviarSolicitud(EnviarSolicitudRequest(tag)) }
    suspend fun obtenerSolicitudes() = safe { api.obtenerSolicitudes() }
    suspend fun aceptarSolicitud(id: Long) = safe { api.aceptarSolicitud(id) }
    suspend fun rechazarSolicitud(id: Long) = safe { api.rechazarSolicitud(id) }
    suspend fun obtenerAmigos() = safe { api.obtenerAmigos() }
    suspend fun eliminarAmigo(id: Long) = safe { api.eliminarAmigo(id) }

    // favoritos
    suspend fun obtenerFavoritos() = safe { api.obtenerFavoritos() }
    suspend fun crearFavorito(f: FavoritoDTO) = safe { api.crearFavorito(f) }
    suspend fun editarFavorito(id: Long, f: FavoritoDTO) = safe { api.editarFavorito(id, f) }
    suspend fun eliminarFavorito(id: Long) = safe { api.eliminarFavorito(id) }

    // invitaciones
    suspend fun enviarInvitacion(idLista: Long, tag: String) = safe { api.enviarInvitacion(EnviarInvitacionRequest(idLista, tag)) }
    suspend fun obtenerInvitaciones() = safe { api.obtenerInvitaciones() }
    suspend fun aceptarInvitacion(id: Long) = safe { api.aceptarInvitacion(id) }
    suspend fun rechazarInvitacion(id: Long) = safe { api.rechazarInvitacion(id) }

    // perfil
    suspend fun cambiarContrasena(actual: String, nueva: String) = safe { api.cambiarContrasena(CambiarContrasenaRequest(actual, nueva)) }
    suspend fun actualizarPerfil(dto: UsuarioDTO) = safe {
        val resp = api.actualizarPerfil(dto)
        resp.usuario?.nombreUsuario?.let { session.saveNombreUsuario(it) }
        resp
    }
    suspend fun actualizarPreferencias(temaOscuro: Boolean) = safe {
        val resp = api.actualizarPreferencias(PreferenciasRequest(temaOscuro))
        session.saveTemaOscuro(resp.temaOscuro)
        resp
    }

    // admin
    suspend fun adminListarUsuarios(q: String? = null) = safe { api.adminListarUsuarios(q) }
    suspend fun adminCambiarRol(id: Long, rol: String) = safe { api.adminCambiarRol(id, CambiarRolRequest(rol)) }
    suspend fun adminEliminarUsuario(id: Long) = safe { api.adminEliminarUsuario(id) }

    // presets — directos a la API
    suspend fun obtenerSupermercadosDefecto() = safe { api.obtenerSupermercadosDefecto() }
    suspend fun obtenerProductosDefecto() = safe { api.obtenerProductosDefecto() }

    /**
     * Devuelve los productos por defecto cacheados localmente.
     * La primera vez los descarga de la API y los guarda en almacenamiento local;
     * a partir de ahí siempre vienen del cache, igual que los favoritos del usuario.
     */
    suspend fun obtenerProductosDefectoCacheados(): Result<List<ProductoCatalogoDTO>> = runCatching {
        presets.productosCacheados()?.let { return@runCatching it }
        val frescos = api.obtenerProductosDefecto()
        presets.guardarProductos(frescos)
        frescos
    }.recoverCatching { throw mapError(it) }

    suspend fun obtenerSupermercadosDefectoCacheados(): Result<List<String>> = runCatching {
        presets.supermercadosCacheados()?.let { return@runCatching it }
        val frescos = api.obtenerSupermercadosDefecto()
        presets.guardarSupermercados(frescos)
        frescos
    }.recoverCatching { throw mapError(it) }

    /** Borra el cache local (útil tras logout o si se quiere forzar refresco). */
    suspend fun limpiarPresetsCache() = presets.limpiar()

    private suspend fun <T> safe(block: suspend () -> T): Result<T> = runCatching { block() }
        .recoverCatching { throw mapError(it) }

    private fun mapError(e: Throwable): Throwable {
        if (e is HttpException) {
            val msg = try {
                val body = e.response()?.errorBody()?.string()
                if (!body.isNullOrEmpty()) {
                    val json = org.json.JSONObject(body)
                    json.optString("message").ifEmpty { "Error ${e.code()}" }
                } else "Error ${e.code()}"
            } catch (_: Exception) {
                "Error ${e.code()}"
            }
            return RuntimeException(msg)
        }
        return RuntimeException(e.message ?: "Error de red")
    }
}
