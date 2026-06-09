package com.example.tfg.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.tfg.data.model.FavoritoDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoritosState(
    val loading: Boolean = false,
    val favoritos: List<FavoritoDTO> = emptyList(),
    val mensaje: String? = null,
    val error: String? = null
)

class FavoritosViewModel : BaseVM() {
    private val _state = MutableStateFlow(FavoritosState())
    val state = _state.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            repo.obtenerFavoritos()
                .onSuccess { _state.value = _state.value.copy(loading = false, favoritos = it) }
                .onFailure { _state.value = _state.value.copy(loading = false, error = it.message ?: "No se pudieron cargar los favoritos") }
        }
    }

    fun crear(f: FavoritoDTO, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.crearFavorito(f)
                .onSuccess { _state.value = _state.value.copy(mensaje = "Favorito creado"); cargar(); onDone() }
                .onFailure { _state.value = _state.value.copy(error = it.message ?: "No se pudo crear el favorito") }
        }
    }

    fun editar(id: Long, f: FavoritoDTO, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.editarFavorito(id, f)
                .onSuccess { _state.value = _state.value.copy(mensaje = "Favorito guardado"); cargar(); onDone() }
                .onFailure { _state.value = _state.value.copy(error = it.message ?: "No se pudo guardar") }
        }
    }

    fun eliminar(id: Long) {
        viewModelScope.launch {
            repo.eliminarFavorito(id)
                .onSuccess { _state.value = _state.value.copy(mensaje = "Favorito eliminado"); cargar() }
                .onFailure { _state.value = _state.value.copy(error = it.message ?: "No se pudo eliminar") }
        }
    }

    fun limpiarMensaje() {
        _state.value = _state.value.copy(mensaje = null, error = null)
    }
}
