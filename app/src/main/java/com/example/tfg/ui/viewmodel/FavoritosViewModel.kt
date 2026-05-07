package com.example.tfg.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.tfg.data.model.FavoritoDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoritosState(
    val loading: Boolean = false,
    val favoritos: List<FavoritoDTO> = emptyList(),
    val error: String? = null
)

class FavoritosViewModel : BaseVM() {
    private val _state = MutableStateFlow(FavoritosState())
    val state = _state.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            repo.obtenerFavoritos()
                .onSuccess { _state.value = FavoritosState(favoritos = it) }
                .onFailure { _state.value = FavoritosState(error = it.message) }
        }
    }

    fun crear(f: FavoritoDTO, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.crearFavorito(f).onSuccess { cargar(); onDone() }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
        }
    }

    fun editar(id: Long, f: FavoritoDTO, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.editarFavorito(id, f).onSuccess { cargar(); onDone() }
        }
    }

    fun eliminar(id: Long) {
        viewModelScope.launch {
            repo.eliminarFavorito(id).onSuccess { cargar() }
        }
    }
}
