package com.example.tfg.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.tfg.data.model.AdminUsuarioDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminState(
    val loading: Boolean = false,
    val usuarios: List<AdminUsuarioDTO> = emptyList(),
    val query: String = "",
    val mensaje: String? = null,
    val error: String? = null
)

class AdminViewModel : BaseVM() {
    private val _state = MutableStateFlow(AdminState())
    val state = _state.asStateFlow()

    fun cargar(q: String? = _state.value.query.ifBlank { null }) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            repo.adminListarUsuarios(q)
                .onSuccess { _state.value = _state.value.copy(loading = false, usuarios = it, error = null) }
                .onFailure { _state.value = _state.value.copy(loading = false, error = it.message ?: "No se pudieron cargar los usuarios") }
        }
    }

    fun setQuery(q: String) {
        _state.value = _state.value.copy(query = q)
        cargar(q.ifBlank { null })
    }

    fun cambiarRol(id: Long, nuevoRol: String) {
        viewModelScope.launch {
            repo.adminCambiarRol(id, nuevoRol)
                .onSuccess { _state.value = _state.value.copy(mensaje = "Rol actualizado a $nuevoRol"); cargar() }
                .onFailure { _state.value = _state.value.copy(error = it.message ?: "No se pudo cambiar el rol") }
        }
    }

    fun eliminar(id: Long) {
        viewModelScope.launch {
            repo.adminEliminarUsuario(id)
                .onSuccess { _state.value = _state.value.copy(mensaje = "Usuario eliminado"); cargar() }
                .onFailure { _state.value = _state.value.copy(error = it.message ?: "No se pudo eliminar") }
        }
    }

    fun limpiarMensaje() {
        _state.value = _state.value.copy(mensaje = null, error = null)
    }
}
