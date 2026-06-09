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
    val mensaje: String? = null,
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
            _state.value = _state.value.copy(loading = false, solicitudes = sols, invitaciones = invs)
        }
    }

    fun aceptarSolicitud(id: Long) = viewModelScope.launch {
        repo.aceptarSolicitud(id)
            .onSuccess { _state.value = _state.value.copy(mensaje = "Solicitud aceptada"); cargar() }
            .onFailure { _state.value = _state.value.copy(error = it.message ?: "No se pudo aceptar") }
    }

    fun rechazarSolicitud(id: Long) = viewModelScope.launch {
        repo.rechazarSolicitud(id)
            .onSuccess { _state.value = _state.value.copy(mensaje = "Solicitud rechazada"); cargar() }
            .onFailure { _state.value = _state.value.copy(error = it.message ?: "No se pudo rechazar") }
    }

    fun aceptarInvitacion(id: Long) = viewModelScope.launch {
        repo.aceptarInvitacion(id)
            .onSuccess { _state.value = _state.value.copy(mensaje = "Invitación aceptada"); cargar() }
            .onFailure { _state.value = _state.value.copy(error = it.message ?: "No se pudo aceptar") }
    }

    fun rechazarInvitacion(id: Long) = viewModelScope.launch {
        repo.rechazarInvitacion(id)
            .onSuccess { _state.value = _state.value.copy(mensaje = "Invitación rechazada"); cargar() }
            .onFailure { _state.value = _state.value.copy(error = it.message ?: "No se pudo rechazar") }
    }

    fun limpiarMensaje() {
        _state.value = _state.value.copy(mensaje = null, error = null)
    }
}
