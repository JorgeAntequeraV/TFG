package com.example.tfg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.tfg.data.model.AdminUsuarioDTO
import com.example.tfg.ui.components.GreenTextField
import com.example.tfg.ui.components.PrimaryButton
import com.example.tfg.ui.theme.GreenAccent
import com.example.tfg.ui.theme.GreenDark
import com.example.tfg.ui.theme.GreenMedium
import com.example.tfg.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(onBack: () -> Unit) {
    val vm: AdminViewModel = viewModel()
    val state by vm.state.collectAsState()
    val toast = com.example.tfg.ui.components.rememberToast()
    LaunchedEffect(Unit) { vm.cargar() }
    LaunchedEffect(state.mensaje) { state.mensaje?.let { toast.exito(it); vm.limpiarMensaje() } }
    LaunchedEffect(state.error) { state.error?.let { toast.error(it); vm.limpiarMensaje() } }

    var seleccion by remember { mutableStateOf<AdminUsuarioDTO?>(null) }
    var confirmEliminar by remember { mutableStateOf<AdminUsuarioDTO?>(null) }

    val config = LocalConfiguration.current
    val padTop = config.screenHeightDp.dp / 16
    val padLR = config.screenWidthDp.dp / 32
    val gap = config.screenHeightDp.dp / 32

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0)
    ) { inner ->
        Box(Modifier.padding(inner).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
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
                Text("Panel de Administración",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(gap))
            GreenTextField(
                value = state.query,
                onValueChange = { vm.setQuery(it) },
                placeholder = "Buscar por usuario, tag o correo...",
                background = GreenDark
            )
            Spacer(Modifier.height(16.dp))
            if (state.loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            state.error?.let { Text(it, color = Color.Red) }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.usuarios, key = { it.id }) { u ->
                    UsuarioCard(u, onClick = { seleccion = u })
                }
            }
        }
        com.example.tfg.ui.components.BackBoton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(start = padLR)
        )
        }
    }

    seleccion?.let { u ->
        ModalBottomSheet(onDismissRequest = { seleccion = null }, containerColor = GreenMedium) {
            OpcionesAdminSheet(
                usuario = u,
                onCambiarRol = {
                    val nuevo = if (u.rol == "ADMIN") "USER" else "ADMIN"
                    vm.cambiarRol(u.id, nuevo)
                    seleccion = null
                },
                onEliminar = {
                    confirmEliminar = u
                    seleccion = null
                },
                onCerrar = { seleccion = null }
            )
        }
    }

    confirmEliminar?.let { u ->
        AlertDialog(
            onDismissRequest = { confirmEliminar = null },
            title = { Text("Eliminar usuario") },
            text = { Text("¿Seguro que quieres eliminar a ${u.nombreUsuario}? Esta acción es permanente.") },
            confirmButton = {
                TextButton(onClick = { vm.eliminar(u.id); confirmEliminar = null }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun UsuarioCard(u: AdminUsuarioDTO, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(GreenMedium, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(u.nombreUsuario ?: "?", color = Color.White, fontWeight = FontWeight.Bold)
            Text(u.email ?: "", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(u.rol ?: "USER", color = if (u.rol == "ADMIN") GreenAccent else Color.White,
                fontWeight = FontWeight.Bold)
            Text(u.tagAmigo ?: "", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun OpcionesAdminSheet(
    usuario: AdminUsuarioDTO,
    onCambiarRol: () -> Unit,
    onEliminar: () -> Unit,
    onCerrar: () -> Unit
) {
    Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Gestionando a: ${usuario.nombreUsuario}",
            color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        PrimaryButton(
            text = if (usuario.rol == "ADMIN") "Quitar Administrador" else "Hacer Administrador",
            onClick = onCambiarRol,
            modifier = Modifier.fillMaxWidth()
        )
        PrimaryButton("Eliminar Usuario", onEliminar, Modifier.fillMaxWidth())
        PrimaryButton("Cerrar", onCerrar, Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
    }
}
