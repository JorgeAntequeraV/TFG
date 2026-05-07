package com.example.tfg.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.tfg.data.model.UsuarioDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

data class PerfilState(
    val nombreUsuario: String? = null,
    val rol: String? = null,
    val temaOscuro: Boolean = false,
    val mensaje: String? = null,
    val error: String? = null
)

class PerfilViewModel : BaseVM() {

    val sessionState = combine(
        session.nombreUsuarioFlow,
        session.rolFlow,
        session.temaOscuroFlow
    ) { nombre, rol, tema ->
        PerfilState(nombreUsuario = nombre, rol = rol, temaOscuro = tema)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, PerfilState())

    private val _state = MutableStateFlow(PerfilState())
    val state = _state.asStateFlow()

    fun toggleTemaOscuro(value: Boolean) {
        viewModelScope.launch {
            session.saveTemaOscuro(value)
            repo.actualizarPreferencias(value)
        }
    }

    fun cambiarNombreUsuario(nuevo: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.actualizarPerfil(UsuarioDTO(nombreUsuario = nuevo))
                .onSuccess { _state.value = _state.value.copy(mensaje = it.message); onDone() }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
        }
    }

    fun cambiarCorreo(nuevoEmail: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.actualizarPerfil(UsuarioDTO(email = nuevoEmail))
                .onSuccess { _state.value = _state.value.copy(mensaje = it.message); onDone() }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
        }
    }

    fun cambiarContrasena(actual: String, nueva: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.cambiarContrasena(actual, nueva)
                .onSuccess { _state.value = _state.value.copy(mensaje = it.message); onDone() }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch { repo.logout(); onDone() }
    }

    fun limpiar() { _state.value = _state.value.copy(error = null, mensaje = null) }
}
