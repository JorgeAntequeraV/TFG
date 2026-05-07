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
            val sis = repo.obtenerProductosDefecto().getOrNull() ?: emptyList()
            _state.value = AddItemState(favoritos = favs, sistema = sis)
        }
    }

    fun anadirNuevo(idLista: Long, p: ProductoListaDTO, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.anadirItem(idLista, p).onSuccess { onDone() }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
        }
    }

    fun anadirDesdeFavorito(idLista: Long, idFav: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.anadirDesdeFavorito(idLista, idFav).onSuccess { onDone() }
        }
    }

    fun anadirDesdeSistema(idLista: Long, prod: ProductoCatalogoDTO, onDone: () -> Unit) {
        viewModelScope.launch {
            val nuevo = ProductoListaDTO(
                nombre = prod.nombre,
                cantidad = prod.cantidad,
                unidadMedida = null,
                precio = prod.precio
            )
            repo.anadirItem(idLista, nuevo).onSuccess { onDone() }
        }
    }
}
