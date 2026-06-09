package com.example.tfg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tfg.data.model.FavoritoDTO
import com.example.tfg.data.model.ProductoCatalogoDTO
import com.example.tfg.data.model.ProductoListaDTO
import com.example.tfg.ui.theme.GreenAccent
import com.example.tfg.ui.theme.GreenDark
import com.example.tfg.ui.theme.GreenMedium
import com.example.tfg.ui.viewmodel.AddItemViewModel

@Composable
fun AddItemScreen(listaId: Long, onDone: () -> Unit) {
    val vm: AddItemViewModel = viewModel()
    val state by vm.state.collectAsState()
    var tab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) { vm.cargar() }

    val toast = com.example.tfg.ui.components.rememberToast()
    LaunchedEffect(state.error) { state.error?.let { toast.error(it) } }

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
            Text("Añadir producto", color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            TabHeader("Predefinidos", tab == 0, Modifier.weight(1f)) { tab = 0 }
            TabHeader("Añadir uno nuevo", tab == 1, Modifier.weight(1f)) { tab = 1 }
        }
        Spacer(Modifier.height(8.dp))
        state.error?.let { Text(it, color = Color.Red, fontSize = 13.sp); Spacer(Modifier.height(8.dp)) }

        if (tab == 0) {
            PredefinidosTab(
                favoritos = state.favoritos,
                sistema = state.sistema,
                onFavoritoClick = { f -> f.id?.let { vm.anadirDesdeFavorito(listaId, it, onDone) } },
                onSistemaClick = { p -> vm.anadirDesdeSistema(listaId, p, onDone) }
            )
        } else {
            NuevoItemTab(onCrear = { dto -> vm.anadirNuevo(listaId, dto, onDone) })
        }
    }
        com.example.tfg.ui.components.BackBoton(
            onClick = onDone,
            modifier = Modifier.align(Alignment.BottomStart).padding(start = padLR)
        )
    }
}

@Composable
private fun TabHeader(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        Spacer(Modifier.height(6.dp))
        Box(Modifier.height(3.dp).fillMaxWidth().background(if (selected) GreenDark else Color.Transparent))
    }
}

@Composable
private fun PredefinidosTab(
    favoritos: List<FavoritoDTO>,
    sistema: List<ProductoCatalogoDTO>,
    onFavoritoClick: (FavoritoDTO) -> Unit,
    onSistemaClick: (ProductoCatalogoDTO) -> Unit
) {
    var openFav by remember { mutableStateOf(true) }
    var openSis by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(start = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Header("Favoritos", openFav) { openFav = !openFav }
        }
        if (openFav) {
            items(favoritos, key = { "fav-${it.id ?: 0}" }) { f ->
                ItemRow(text = formatFav(f), onClick = { onFavoritoClick(f) })
            }
        }
        item { Header("Sistema", openSis) { openSis = !openSis } }
        if (openSis) {
            items(sistema, key = { "sis-${it.id ?: 0}" }) { p ->
                ItemRow(text = p.nombre, onClick = { onSistemaClick(p) })
            }
        }
    }
}

private fun formatFav(f: FavoritoDTO): String {
    val cant = listOfNotNull(f.cantidad?.takeIf { it.isNotBlank() }, f.unidadMedida?.takeIf { it.isNotBlank() }).joinToString(" ")
    return if (cant.isBlank()) f.nombre else "${f.nombre}  $cant"
}

@Composable
private fun Header(label: String, open: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f))
        Icon(if (open) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
            null, tint = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun ItemRow(text: String, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
            .background(GreenMedium, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(text, color = Color.White)
    }
}

@Composable
private fun NuevoItemTab(onCrear: (ProductoListaDTO) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CardField("Introduzca el nombre", nombre, { nombre = it })
            CardField("Introduzca la cantidad", cantidad, { v -> if (v.all { it.isDigit() }) cantidad = v }, KeyboardType.Number)
            CardField("Unidad de medida del producto", unidad, { unidad = it })
            CardField("Introduce el precio", precio, { v -> if (v.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) precio = v }, KeyboardType.Decimal)
        }
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp)
                .size(56.dp)
                .background(GreenAccent, CircleShape)
                .clickable(enabled = nombre.isNotBlank()) {
                    onCrear(ProductoListaDTO(
                        nombre = nombre.trim(),
                        cantidad = cantidad.ifBlank { null },
                        unidadMedida = unidad.ifBlank { null },
                        precio = precio.toDoubleOrNull()
                    ))
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, null, tint = Color.Black)
        }
    }
}

@Composable
fun CardField(
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.7f)) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenMedium, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
            focusedContainerColor = GreenMedium, unfocusedContainerColor = GreenMedium,
            focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent,
            cursorColor = Color.White
        )
    )
}
