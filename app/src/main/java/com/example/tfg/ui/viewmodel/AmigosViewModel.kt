package com.example.tfg.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.tfg.data.model.AmigoDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AmigosState(
    val loading: Boolean = false,
    val amigos: List<AmigoDTO> = emptyList(),
    val miTag: String? = null,
    val mensaje: String? = null,
    val error: String? = null
)

class AmigosViewModel : BaseVM() {
    private val _state = MutableStateFlow(AmigosState())
    val state = _state.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            repo.obtenerAmigos()
                .onSuccess { _state.value = _state.value.copy(loading = false, amigos = it, error = null) }
                .onFailure { _state.value = _state.value.copy(loading = false, error = it.message) }
            // Cargar también el tag propio (no bloquea la lista)
            repo.obtenerMiUsuario()
                .onSuccess { _state.value = _state.value.copy(miTag = it.tagAmigo) }
        }
    }

    fun eliminar(id: Long) {
        viewModelScope.launch {
            repo.eliminarAmigo(id)
                .onSuccess {
                    _state.value = _state.value.copy(mensaje = "Amigo eliminado", error = null)
                    cargar()
                }
                .onFailure { _state.value = _state.value.copy(error = it.message ?: "No se pudo eliminar") }
        }
    }

    fun enviarSolicitud(tag: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.enviarSolicitud(tag.uppercase().trim())
                .onSuccess {
                    _state.value = _state.value.copy(mensaje = "Solicitud enviada", error = null)
                    onDone()
                }
                .onFailure { _state.value = _state.value.copy(error = it.message ?: "No se pudo enviar la solicitud") }
        }
    }

    fun limpiarMensaje() {
        _state.value = _state.value.copy(mensaje = null, error = null)
    }
}
