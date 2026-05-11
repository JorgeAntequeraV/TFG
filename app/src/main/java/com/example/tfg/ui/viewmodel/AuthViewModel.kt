package com.example.tfg.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.tfg.data.model.UsuarioRegistro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class AuthViewModel : BaseVM() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    fun reset() {
        _state.value = AuthState()
    }

    fun login(usuario: String, contrasena: String) {
        viewModelScope.launch {
            _state.value = AuthState(loading = true)
            repo.login(usuario, contrasena)
                .onSuccess { _state.value = AuthState(success = true) }
                .onFailure { _state.value = AuthState(error = it.message ?: "Error") }
        }
    }

    fun loginConGoogle(idToken: String) {
        viewModelScope.launch {
            _state.value = AuthState(loading = true)
            repo.loginConGoogle(idToken)
                .onSuccess { _state.value = AuthState(success = true) }
                .onFailure { _state.value = AuthState(error = it.message ?: "Error con Google") }
        }
    }

    fun mostrarError(mensaje: String) {
        _state.value = _state.value.copy(error = mensaje)
    }

    fun forgotPassword(nombreUsuario: String) {
        viewModelScope.launch {
            _state.value = AuthState(loading = true)
            repo.forgotPassword(nombreUsuario.trim())
                .onSuccess { _state.value = AuthState(success = true) }
                .onFailure { _state.value = AuthState(error = it.message ?: "Error") }
        }
    }

    fun resetPassword(token: String, nuevaContrasena: String) {
        viewModelScope.launch {
            _state.value = AuthState(loading = true)
            repo.resetPassword(token, nuevaContrasena)
                .onSuccess { _state.value = AuthState(success = true) }
                .onFailure { _state.value = AuthState(error = it.message ?: "Error") }
        }
    }

    fun registro(
        nombre: String,
        email: String,
        usuario: String,
        contrasena: String,
        recaptchaToken: String? = null
    ) {
        viewModelScope.launch {
            _state.value = AuthState(loading = true)
            val req = UsuarioRegistro(nombre, email, usuario, contrasena, null)
            repo.registro(req, recaptchaToken)
                .onSuccess {
                    // tras registro hay que loguear automáticamente
                    repo.login(usuario, contrasena)
                        .onSuccess { _state.value = AuthState(success = true) }
                        .onFailure { _state.value = AuthState(error = it.message ?: "Error") }
                }
                .onFailure { _state.value = AuthState(error = it.message ?: "Error") }
        }
    }
}
