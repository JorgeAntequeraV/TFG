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
                .onFailure { _state.value = _state.value.copy(loading = false, error = it.message) }
        }
    }

    fun setQuery(q: String) {
        _state.value = _state.value.copy(query = q)
        cargar(q.ifBlank { null })
    }

    fun cambiarRol(id: Long, nuevoRol: String) {
        viewModelScope.launch {
            repo.adminCambiarRol(id, nuevoRol).onSuccess { cargar() }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
        }
    }

    fun eliminar(id: Long) {
        viewModelScope.launch {
            repo.adminEliminarUsuario(id).onSuccess { cargar() }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
        }
    }
}
