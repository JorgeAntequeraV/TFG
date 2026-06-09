package com.example.tfg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tfg.data.model.InvitacionListaDTO
import com.example.tfg.data.model.SolicitudAmistadDTO
import com.example.tfg.ui.theme.GreenAccent
import com.example.tfg.ui.theme.GreenDark
import com.example.tfg.ui.theme.GreenMedium
import com.example.tfg.ui.viewmodel.NotificacionesViewModel

@Composable
fun NotificacionesScreen(onBack: () -> Unit) {
    val vm: NotificacionesViewModel = viewModel()
    val state by vm.state.collectAsState()
    val toast = com.example.tfg.ui.components.rememberToast()
    LaunchedEffect(Unit) { vm.cargar() }
    LaunchedEffect(state.mensaje) { state.mensaje?.let { toast.exito(it); vm.limpiarMensaje() } }
    LaunchedEffect(state.error) { state.error?.let { toast.error(it); vm.limpiarMensaje() } }

    val config = LocalConfiguration.current
    val padTop = config.screenHeightDp.dp / 16
    val padLR = config.screenWidthDp.dp / 32

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = padLR)
            .padding(top = padTop)
    ) {
        Row(
            Modifier.fillMaxWidth().height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Notificaciones",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp, fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(16.dp))

        if (state.loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        state.error?.let { Text(it, color = Color.Red) }

        if (!state.loading && state.solicitudes.isEmpty() && state.invitaciones.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Sin notificaciones", color = MaterialTheme.colorScheme.onBackground)
            }
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (state.solicitudes.isNotEmpty()) {
                item { SectionTitle("Solicitudes de amistad") }
                items(state.solicitudes, key = { "s-${it.id}" }) { s ->
                    SolicitudCard(
                        s,
                        onAceptar = { vm.aceptarSolicitud(s.id) },
                        onRechazar = { vm.rechazarSolicitud(s.id) }
                    )
                }
                item { Spacer(Modifier.height(12.dp)) }
            }
            if (state.invitaciones.isNotEmpty()) {
                item { SectionTitle("Invitaciones a listas") }
                items(state.invitaciones, key = { "i-${it.id}" }) { i ->
                    InvitacionCard(
                        i,
                        onAceptar = { vm.aceptarInvitacion(i.id) },
                        onRechazar = { vm.rechazarInvitacion(i.id) }
                    )
                }
            }
        }
    }
        com.example.tfg.ui.components.BackBoton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(start = padLR)
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun SolicitudCard(s: SolicitudAmistadDTO, onAceptar: () -> Unit, onRechazar: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(GreenMedium, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(s.nombreRemitente ?: "?", color = Color.White, fontWeight = FontWeight.Bold)
            Text(s.tagRemitente ?: "", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
        }
        AccionIcon(Icons.Default.Check, GreenAccent, onAceptar)
        Spacer(Modifier.width(8.dp))
        AccionIcon(Icons.Default.Close, GreenDark, onRechazar)
    }
}

@Composable
private fun InvitacionCard(i: InvitacionListaDTO, onAceptar: () -> Unit, onRechazar: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(GreenMedium, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(i.nombreLista ?: "?", color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                "Invita: ${i.nombreAnfitrion ?: ""}",
                color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp
            )
        }
        AccionIcon(Icons.Default.Check, GreenAccent, onAceptar)
        Spacer(Modifier.width(8.dp))
        AccionIcon(Icons.Default.Close, GreenDark, onRechazar)
    }
}

@Composable
private fun AccionIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bg: Color,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .size(40.dp)
            .background(bg, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = if (bg == GreenAccent) Color.Black else Color.White)
    }
}
