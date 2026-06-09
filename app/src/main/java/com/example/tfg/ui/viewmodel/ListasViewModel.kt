package com.example.tfg.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.tfg.data.model.ListaDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ListasState(
    val loading: Boolean = false,
    val listas: List<ListaDTO> = emptyList(),
    val mensaje: String? = null,
    val error: String? = null
)

class ListasViewModel : BaseVM() {
    private val _state = MutableStateFlow(ListasState())
    val state = _state.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            repo.obtenerListas()
                .onSuccess { _state.value = _state.value.copy(loading = false, listas = it) }
                .onFailure { _state.value = _state.value.copy(loading = false, error = it.message ?: "No se pudieron cargar las listas") }
        }
    }

    fun crear(nombre: String, supermercado: String? = null, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.crearLista(nombre, supermercado)
                .onSuccess {
                    _state.value = _state.value.copy(mensaje = "Lista creada")
                    cargar()
                    onDone()
                }
                .onFailure { _state.value = _state.value.copy(error = it.message ?: "No se pudo crear la lista") }
        }
    }

    fun eliminar(id: Long) {
        viewModelScope.launch {
            repo.eliminarLista(id)
                .onSuccess { _state.value = _state.value.copy(mensaje = "Lista eliminada"); cargar() }
                .onFailure { _state.value = _state.value.copy(error = it.message ?: "No se pudo eliminar") }
        }
    }

    fun limpiarMensaje() {
        _state.value = _state.value.copy(mensaje = null, error = null)
    }
}
