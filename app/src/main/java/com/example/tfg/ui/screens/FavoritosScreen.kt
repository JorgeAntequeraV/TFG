package com.example.tfg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tfg.data.model.FavoritoDTO
import com.example.tfg.ui.components.BottomBar
import com.example.tfg.ui.components.BottomTab
import com.example.tfg.ui.theme.GreenAccent
import com.example.tfg.ui.theme.GreenDark
import com.example.tfg.ui.theme.GreenMedium
import com.example.tfg.ui.viewmodel.FavoritosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    onAdd: () -> Unit,
    onTabChange: (BottomTab) -> Unit
) {
    val vm: FavoritosViewModel = viewModel()
    val state by vm.state.collectAsState()
    val toast = com.example.tfg.ui.components.rememberToast()
    LaunchedEffect(Unit) { vm.cargar() }
    LaunchedEffect(state.mensaje) { state.mensaje?.let { toast.exito(it); vm.limpiarMensaje() } }
    LaunchedEffect(state.error) { state.error?.let { toast.error(it); vm.limpiarMensaje() } }

    var editar by remember { mutableStateOf<FavoritoDTO?>(null) }

    val config = LocalConfiguration.current
    val padTop = config.screenHeightDp.dp / 16
    val padLR = config.screenWidthDp.dp / 32

    Scaffold(
        bottomBar = { BottomBar(BottomTab.FAVORITOS, onTabChange) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0)
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = padLR)
                .padding(top = padTop)
        ) {
            Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Items favoritos", color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Box(
                    Modifier
                        .size(44.dp)
                        .background(GreenAccent, CircleShape)
                        .clickable { onAdd() },
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Add, null, tint = Color.Black) }
            }
            Spacer(Modifier.height(16.dp))

            if (state.loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            state.error?.let { Text(it, color = Color.Red) }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.favoritos, key = { it.id ?: 0 }) { f ->
                    FavoritoCard(f, onTap = { editar = f }, onLong = { f.id?.let(vm::eliminar) })
                }
            }
        }
    }

    editar?.let { f ->
        ModalBottomSheet(onDismissRequest = { editar = null }, containerColor = GreenMedium) {
            EditarFavoritoSheet(
                inicial = f,
                onGuardar = { nuevo -> f.id?.let { vm.editar(it, nuevo) { editar = null } } },
                onEliminar = { f.id?.let { vm.eliminar(it); editar = null } }
            )
        }
    }
}

@Composable
private fun FavoritoCard(f: FavoritoDTO, onTap: () -> Unit, onLong: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(GreenMedium, RoundedCornerShape(12.dp))
            .pointerInput(f.id) {
                detectTapGestures(onTap = { onTap() }, onLongPress = { onLong() })
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(f.nombre, color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(16.dp))
        f.cantidad?.takeIf { it.isNotBlank() }?.let {
            Text(it, color = Color.White)
            Spacer(Modifier.width(4.dp))
            f.unidadMedida?.takeIf { u -> u.isNotBlank() }?.let { u -> Text(u, color = Color.White) }
        }
        Spacer(Modifier.weight(1f))
        f.precio?.let {
            Text(String.format("%.2f €", it), color = Color.White,
                modifier = Modifier
                    .background(GreenDark, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp))
        }
    }
}

@Composable
private fun EditarFavoritoSheet(
    inicial: FavoritoDTO,
    onGuardar: (FavoritoDTO) -> Unit,
    onEliminar: () -> Unit
) {
    var nombre by remember { mutableStateOf(inicial.nombre) }
    var cantidad by remember { mutableStateOf(inicial.cantidad ?: "") }
    var unidad by remember { mutableStateOf(inicial.unidadMedida ?: "") }
    var precio by remember { mutableStateOf(inicial.precio?.toString() ?: "") }

    Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Editar favorito", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        CardField("Nombre", nombre, { nombre = it })
        CardField("Cantidad", cantidad, { v -> if (v.all { it.isDigit() }) cantidad = v },
            androidx.compose.ui.text.input.KeyboardType.Number)
        CardField("Unidad de medida", unidad, { unidad = it })
        CardField("Precio", precio,
            { v -> if (v.matches(Regex("^\\d*(\\.\\d{0,2})?\$"))) precio = v },
            androidx.compose.ui.text.input.KeyboardType.Decimal)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            com.example.tfg.ui.components.PrimaryButton("Eliminar", onEliminar, Modifier.weight(1f))
            com.example.tfg.ui.components.PrimaryButton("Guardar", {
                onGuardar(inicial.copy(
                    nombre = nombre,
                    cantidad = cantidad.ifBlank { null },
                    unidadMedida = unidad.ifBlank { null },
                    precio = precio.toDoubleOrNull()
                ))
            }, Modifier.weight(1f), enabled = nombre.isNotBlank())
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun AddFavoritoScreen(onDone: () -> Unit) {
    val vm: FavoritosViewModel = viewModel()
    val state by vm.state.collectAsState()
    val toast = com.example.tfg.ui.components.rememberToast()
    LaunchedEffect(state.mensaje) { state.mensaje?.let { toast.exito(it); vm.limpiarMensaje() } }
    LaunchedEffect(state.error) { state.error?.let { toast.error(it); vm.limpiarMensaje() } }
    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    val config = LocalConfiguration.current
    val padTop = config.screenHeightDp.dp / 16
    val padLR = config.screenWidthDp.dp / 32

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = padLR)
            .padding(top = padTop)
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth().height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Crear nuevo favorito",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(config.screenHeightDp.dp / 32))
            Column(verticalArrangement = Arrangement.spacedBy(config.screenHeightDp.dp / 32)) {
                CardField("Introduzca el nombre", nombre, { nombre = it })
                CardField("Introduzca la cantidad", cantidad,
                    { v -> if (v.all { it.isDigit() }) cantidad = v },
                    androidx.compose.ui.text.input.KeyboardType.Number)
                CardField("Unidad de medida del producto", unidad, { unidad = it })
                CardField("Introduce el precio", precio,
                    { v -> if (v.matches(Regex("^\\d*(\\.\\d{0,2})?\$"))) precio = v },
                    androidx.compose.ui.text.input.KeyboardType.Decimal)
            }
        }
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp)
                .size(56.dp)
                .background(GreenAccent, CircleShape)
                .clickable(enabled = nombre.isNotBlank()) {
                    vm.crear(FavoritoDTO(
                        nombre = nombre.trim(),
                        cantidad = cantidad.ifBlank { null },
                        unidadMedida = unidad.ifBlank { null },
                        precio = precio.toDoubleOrNull()
                    ), onDone)
                },
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.Add, null, tint = Color.Black) }
    }
}
