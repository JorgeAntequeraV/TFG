package com.example.tfg.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.tfg.data.model.FavoritoDTO
import com.example.tfg.data.model.ProductoCatalogoDTO
import com.example.tfg.data.model.ProductoListaDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddItemState(
    val favoritos: List<FavoritoDTO> = emptyList(),
    val sistema: List<ProductoCatalogoDTO> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class AddItemViewModel : BaseVM() {
    private val _state = MutableStateFlow(AddItemState())
    val state = _state.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            val favs = repo.obtenerFavoritos().getOrNull() ?: emptyList()
            // Los presets de sistema usan cache local excepto la primera vez los pide a la API, mira si es null basicamente
            // y los guarda en el almacenamiento del teléfono, a partir de ahí van del cache.
            val sis = repo.obtenerProductosDefectoCacheados().getOrNull() ?: emptyList()
            _state.value = AddItemState(favoritos = favs, sistema = sis)
        }
    }

    fun anadirNuevo(idLista: Long, p: ProductoListaDTO, onDone: () -> Unit) {
        viewModelScope.launch {
            runCatching {
                repo.anadirItem(idLista, p)
                    .onSuccess { onDone() }
                    .onFailure { _state.value = _state.value.copy(error = it.message ?: "Error al añadir") }
            }.onFailure {
                _state.value = _state.value.copy(error = it.message ?: "Error al añadir")
            }
        }
    }

    fun anadirDesdeFavorito(idLista: Long, idFav: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            runCatching {
                repo.anadirDesdeFavorito(idLista, idFav)
                    .onSuccess { onDone() }
                    .onFailure { _state.value = _state.value.copy(error = it.message ?: "Error al añadir") }
            }.onFailure {
                _state.value = _state.value.copy(error = it.message ?: "Error al añadir")
            }
        }
    }

    fun anadirDesdeSistema(idLista: Long, prod: ProductoCatalogoDTO, onDone: () -> Unit) {
        viewModelScope.launch {
            runCatching {
                // Limpiamos valores vacíos para que la API no se queje: "" → null, 0.0 → null
                val nuevo = ProductoListaDTO(
                    nombre = prod.nombre,
                    cantidad = prod.cantidad?.takeIf { it.isNotBlank() },
                    unidadMedida = null,
                    precio = prod.precio?.takeIf { it > 0.0 }
                )
                repo.anadirItem(idLista, nuevo)
                    .onSuccess { onDone() }
                    .onFailure { _state.value = _state.value.copy(error = it.message ?: "Error al añadir") }
            }.onFailure {
                _state.value = _state.value.copy(error = it.message ?: "Error al añadir")
            }
        }
    }
}
