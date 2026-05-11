package com.example.tfg.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.tfg.data.model.ListaDTO
import com.example.tfg.data.model.ProductoListaDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DentroListaState(
    val loading: Boolean = false,
    val lista: ListaDTO? = null,
    val seleccion: Set<Long> = emptySet(),
    val error: String? = null
)

class DentroListaViewModel : BaseVM() {

    private val _state = MutableStateFlow(DentroListaState())
    val state = _state.asStateFlow()

    private var listaId: Long = -1

    fun cargar(id: Long) {
        listaId = id
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            repo.obtenerLista(id)
                .onSuccess { _state.value = DentroListaState(lista = it) }
                .onFailure { _state.value = _state.value.copy(loading = false, error = it.message) }
        }
    }

    fun renombrar(nombre: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.renombrarLista(listaId, nombre).onSuccess {
                cargar(listaId); onDone()
            }
        }
    }

    fun configurar(asc: Boolean, mostrarPrecios: Boolean, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.configurarLista(listaId, asc, mostrarPrecios).onSuccess {
                cargar(listaId); onDone()
            }
        }
    }

    fun anadirItem(p: ProductoListaDTO, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.anadirItem(listaId, p).onSuccess {
                cargar(listaId); onDone()
            }.onFailure { _state.value = _state.value.copy(error = it.message) }
        }
    }

    fun editarItem(idProd: Long, p: ProductoListaDTO, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.editarItem(listaId, idProd, p).onSuccess {
                cargar(listaId); onDone()
            }
        }
    }

    fun eliminarItem(idProd: Long) {
        viewModelScope.launch {
            repo.eliminarItem(listaId, idProd).onSuccess { cargar(listaId) }
        }
    }

    fun eliminarSeleccionados() {
        viewModelScope.launch {
            _state.value.seleccion.forEach { repo.eliminarItem(listaId, it) }
            limpiarSeleccion()
            cargar(listaId)
        }
    }


    fun eliminarVarios(ids: Collection<Long>, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            var fallos = 0
            ids.forEach { id ->
                repo.eliminarItem(listaId, id).onFailure { fallos++ }
            }
            if (fallos > 0) {
                _state.value = _state.value.copy(error = "No se pudieron eliminar $fallos producto(s)")
            }
            cargar(listaId)
            onDone()
        }
    }

    fun copiarSeleccionadosA(idDestino: Long, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.copiarItems(idDestino, _state.value.seleccion.toList())
                .onSuccess {
                    limpiarSeleccion()
                    onDone()
                }
                .onFailure {
                    _state.value = _state.value.copy(error = it.message ?: "Error al copiar")
                    onDone() // cierra el sheet aunque falle, para no dejar al usuario atrapado
                }
        }
    }

    fun anadirDesdeFavorito(idFav: Long, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.anadirDesdeFavorito(listaId, idFav).onSuccess {
                cargar(listaId); onDone()
            }
        }
    }

    fun toggleSeleccion(id: Long) {
        val s = _state.value.seleccion.toMutableSet()
        if (id in s) s.remove(id) else s.add(id)
        _state.value = _state.value.copy(seleccion = s)
    }

    fun limpiarSeleccion() {
        _state.value = _state.value.copy(seleccion = emptySet())
    }

    fun compartirCon(tag: String, onDone: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            repo.enviarInvitacion(listaId, tag)
                .onSuccess { onDone() }
                .onFailure { onError(it.message ?: "Error") }
        }
    }
}
