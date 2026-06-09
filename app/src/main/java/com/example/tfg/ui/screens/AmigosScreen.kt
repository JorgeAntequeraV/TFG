package com.example.tfg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.example.tfg.data.model.AmigoDTO
import com.example.tfg.ui.components.GreenTextField
import com.example.tfg.ui.components.PrimaryButton
import com.example.tfg.ui.components.rememberToast
import com.example.tfg.ui.theme.GreenAccent
import com.example.tfg.ui.theme.GreenDark
import com.example.tfg.ui.theme.GreenMedium
import com.example.tfg.ui.viewmodel.AmigosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmigosScreen(onBack: () -> Unit) {
    val vm: AmigosViewModel = viewModel()
    val state by vm.state.collectAsState()
    val toast = rememberToast()
    var showAdd by remember { mutableStateOf(false) }
    var confirmEliminar by remember { mutableStateOf<AmigoDTO?>(null) }

    LaunchedEffect(Unit) { vm.cargar() }
    LaunchedEffect(state.error) { state.error?.let { toast.error(it); vm.limpiarMensaje() } }
    LaunchedEffect(state.mensaje) { state.mensaje?.let { toast.exito(it); vm.limpiarMensaje() } }

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
                "Mis amigos",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp, fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            // Tag del usuario actual (su identificador para que otros le añadan)
            Box(
                Modifier
                    .background(GreenMedium, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    state.miTag ?: "…",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.weight(1f))
            Box(
                Modifier
                    .size(44.dp)
                    .background(GreenAccent, CircleShape)
                    .clickable { showAdd = true },
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Add, null, tint = Color.Black) }
        }
        Spacer(Modifier.height(16.dp))

        if (state.loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        state.error?.let { Text(it, color = Color.Red) }
        state.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.onBackground) }

        if (!state.loading && state.amigos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Aún no tienes amigos. Pulsa + para añadir uno con su tag.",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.amigos, key = { it.id }) { a ->
                AmigoCard(a, onEliminar = { confirmEliminar = a })
            }
        }
    }
        com.example.tfg.ui.components.BackBoton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(start = padLR)
        )
    }

    if (showAdd) {
        ModalBottomSheet(onDismissRequest = { showAdd = false }, containerColor = GreenMedium) {
            AnadirAmigoSheet(
                onEnviar = { tag -> vm.enviarSolicitud(tag) { showAdd = false } },
                error = state.error
            )
        }
    }

    confirmEliminar?.let { a ->
        AlertDialog(
            onDismissRequest = { confirmEliminar = null },
            title = { Text("Eliminar amigo") },
            text = { Text("¿Eliminar a ${a.nombreUsuario}? Se eliminará en ambos sentidos.") },
            confirmButton = {
                TextButton(onClick = { vm.eliminar(a.id); confirmEliminar = null }) {
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
private fun AmigoCard(a: AmigoDTO, onEliminar: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(GreenMedium, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(a.nombreUsuario ?: "?", color = Color.White, fontWeight = FontWeight.Bold)
            Text(a.tagAmigo ?: "", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
        }
        Box(
            Modifier
                .size(40.dp)
                .background(GreenDark, CircleShape)
                .clickable { onEliminar() },
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.Delete, null, tint = Color.White) }
    }
}

@Composable
private fun AnadirAmigoSheet(onEnviar: (String) -> Unit, error: String?) {
    var tag by remember { mutableStateOf("") }
    Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
        Text("Añadir amigo", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        GreenTextField(
            value = tag,
            onValueChange = { tag = it.uppercase().take(8) },
            placeholder = "Tag de amigo (8 caracteres)",
            background = GreenDark
        )
        Spacer(Modifier.height(8.dp))
        error?.let { Text(it, color = Color.Red, fontSize = 13.sp) }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            PrimaryButton(
                "Enviar solicitud",
                { onEnviar(tag) },
                enabled = tag.length == 8
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}
