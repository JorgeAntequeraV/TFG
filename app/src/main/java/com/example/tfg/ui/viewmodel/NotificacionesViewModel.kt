package com.example.tfg.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.tfg.data.model.InvitacionListaDTO
import com.example.tfg.data.model.SolicitudAmistadDTO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificacionesState(
    val loading: Boolean = false,
    val solicitudes: List<SolicitudAmistadDTO> = emptyList(),
    val invitaciones: List<InvitacionListaDTO> = emptyList(),
    val error: String? = null
)

class NotificacionesViewModel : BaseVM() {
    private val _state = MutableStateFlow(NotificacionesState())
    val state = _state.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            val solsDeferred = async { repo.obtenerSolicitudes() }
            val invsDeferred = async { repo.obtenerInvitaciones() }
            val sols = solsDeferred.await().getOrNull().orEmpty()
            val invs = invsDeferred.await().getOrNull().orEmpty()
            _state.value = NotificacionesState(solicitudes = sols, invitaciones = invs)
        }
    }

    fun aceptarSolicitud(id: Long) = viewModelScope.launch {
        repo.aceptarSolicitud(id).onSuccess { cargar() }
            .onFailure { _state.value = _state.value.copy(error = it.message) }
    }

    fun rechazarSolicitud(id: Long) = viewModelScope.launch {
        repo.rechazarSolicitud(id).onSuccess { cargar() }
    }

    fun aceptarInvitacion(id: Long) = viewModelScope.launch {
        repo.aceptarInvitacion(id).onSuccess { cargar() }
            .onFailure { _state.value = _state.value.copy(error = it.message) }
    }

    fun rechazarInvitacion(id: Long) = viewModelScope.launch {
        repo.rechazarInvitacion(id).onSuccess { cargar() }
    }
}
