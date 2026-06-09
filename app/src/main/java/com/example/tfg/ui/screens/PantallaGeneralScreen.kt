package com.example.tfg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.tfg.ui.components.BottomBar
import com.example.tfg.ui.components.BottomTab
import com.example.tfg.ui.components.GreenTextField
import com.example.tfg.ui.components.PrimaryButton
import com.example.tfg.ui.theme.GreenAccent
import com.example.tfg.ui.theme.GreenDark
import com.example.tfg.ui.theme.GreenMedium
import com.example.tfg.ui.viewmodel.ListasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGeneralScreen(
    onAbrirLista: (Long) -> Unit,
    onTabChange: (BottomTab) -> Unit
) {
    val vm: ListasViewModel = viewModel()
    val state by vm.state.collectAsState()
    var showSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.cargar() }

    val toast = com.example.tfg.ui.components.rememberToast()
    LaunchedEffect(state.mensaje) { state.mensaje?.let { toast.exito(it); vm.limpiarMensaje() } }
    LaunchedEffect(state.error) { state.error?.let { toast.error(it); vm.limpiarMensaje() } }

    val config = LocalConfiguration.current
    val padTop = config.screenHeightDp.dp / 16
    val padLR = config.screenWidthDp.dp / 32

    Scaffold(
        bottomBar = { BottomBar(BottomTab.LISTAS, onTabChange) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0)
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = padLR)
                .padding(top = padTop)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Listas de compra",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    Modifier
                        .size(44.dp)
                        .background(GreenAccent, CircleShape)
                        .clickable { showSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.Black)
                }
            }

            Spacer(Modifier.height(config.screenHeightDp.dp / 20))

            if (state.loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            state.error?.let { Text(it, color = Color.Red) }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(config.screenHeightDp.dp / 40)
            ) {
                items(state.listas, key = { it.id ?: 0 }) { lista ->
                    ListaCard(nombre = lista.nombre, onClick = { lista.id?.let(onAbrirLista) })
                }
            }
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                containerColor = GreenMedium
            ) {
                AnadirListaSheet(
                    onCrear = { nombre ->
                        vm.crear(nombre) { showSheet = false }
                    }
                )
            }
        }
    }
}

@Composable
fun ListaCard(nombre: String, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(GreenDark, RoundedCornerShape(12.dp))
            .border(2.dp, GreenMedium, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Text(nombre, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AnadirListaSheet(onCrear: (String) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Text("Crear nueva lista", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        GreenTextField(
            value = nombre,
            onValueChange = { nombre = it },
            placeholder = "Nombre de la lista",
            background = GreenDark
        )
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            PrimaryButton(
                text = "Crear",
                onClick = { if (nombre.isNotBlank()) onCrear(nombre.trim()) },
                enabled = nombre.isNotBlank()
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}
