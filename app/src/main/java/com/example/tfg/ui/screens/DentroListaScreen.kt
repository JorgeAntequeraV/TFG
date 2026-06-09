package com.example.tfg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tfg.data.model.ListaDTO
import com.example.tfg.data.model.ProductoListaDTO
import com.example.tfg.ui.components.BackBoton
import com.example.tfg.ui.components.GreenTextField
import com.example.tfg.ui.components.PrimaryButton
import com.example.tfg.ui.theme.GreenAccent
import com.example.tfg.ui.theme.GreenDark
import com.example.tfg.ui.theme.GreenMedium
import com.example.tfg.ui.theme.RedComprado
import com.example.tfg.ui.theme.RedCompradoDark
import com.example.tfg.ui.viewmodel.DentroListaViewModel
import com.example.tfg.ui.viewmodel.ListasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DentroListaScreen(
    listaId: Long,
    onBack: () -> Unit,
    onAnadirItem: () -> Unit
) {
    val vm: DentroListaViewModel = viewModel()
    val state by vm.state.collectAsState()

    var showCompartir by remember { mutableStateOf(false) }
    var showOpciones by remember { mutableStateOf(false) }
    var showOpcionesSeleccion by remember { mutableStateOf(false) }
    var showCopiar by remember { mutableStateOf(false) }
    var itemPulsado by remember { mutableStateOf<ProductoListaDTO?>(null) }
    var precioItem by remember { mutableStateOf<ProductoListaDTO?>(null) }

    var modoCompra by remember { mutableStateOf(false) }
    var comprados by remember { mutableStateOf<Set<Long>>(emptySet()) }
    // Si la lista cambia, reseteamos
    LaunchedEffect(listaId) {
        modoCompra = false
        comprados = emptySet()
    }

    LaunchedEffect(listaId) { vm.cargar(listaId) }

    val toast = com.example.tfg.ui.components.rememberToast()
    LaunchedEffect(state.mensaje) { state.mensaje?.let { toast.exito(it); vm.limpiarMensaje() } }
    LaunchedEffect(state.error) { state.error?.let { toast.error(it); vm.limpiarMensaje() } }

    val config = LocalConfiguration.current
    val padTop = config.screenHeightDp.dp / 16
    val padLR = config.screenWidthDp.dp / 32

    val lista = state.lista
    val productos = lista?.productos ?: emptyList()
    val ordenados = if (lista?.ordenAscendente != false) productos.sortedBy { it.nombre.lowercase() }
    else productos.sortedByDescending { it.nombre.lowercase() }
    //En modo compra separamos los pendientes de los comprados
    val pendientes = if (modoCompra) ordenados.filter { it.id !in comprados } else ordenados
    val rojos = if (modoCompra) ordenados.filter { it.id in comprados } else emptyList()

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = padLR)
            .padding(top = padTop, bottom = 16.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    lista?.nombre ?: "",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.PersonAdd,
                    contentDescription = "Compartir",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { showCompartir = true }
                )
                Spacer(Modifier.width(12.dp))
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Opciones",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            if (state.seleccion.isNotEmpty()) showOpcionesSeleccion = true
                            else showOpciones = true
                        }
                )
            }

            Spacer(Modifier.height(12.dp))

            // Botón "Modo compra" centrado
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ModoCompraToggle(
                    activo = modoCompra,
                    onClick = {
                        modoCompra = !modoCompra
                        if (!modoCompra) comprados = emptySet()
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            if (state.loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            state.error?.let { Text(it, color = Color.Red) }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 200.dp)
            ) {
                items(pendientes, key = { it.id ?: 0 }) { p ->
                    ItemCard(
                        producto = p,
                        seleccionado = p.id in state.seleccion,
                        enRojo = false,
                        mostrarPrecio = lista?.mostrarPrecios == true,
                        onTap = {
                            when {
                                modoCompra -> p.id?.let { id -> comprados = comprados + id }
                                state.seleccion.isNotEmpty() -> p.id?.let { vm.toggleSeleccion(it) }
                                else -> itemPulsado = p
                            }
                        },
                        onLong = {
                            if (!modoCompra) p.id?.let { vm.toggleSeleccion(it) }
                        },
                        onPrecioClick = { if (!modoCompra) precioItem = p }
                    )
                }
                if (lista?.mostrarPrecios == true) {
                    item {

                        if (modoCompra && rojos.isNotEmpty()) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Total pendiente:",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    String.format("%.2f €", totalDeProductos(pendientes)),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total:", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                                Text(
                                    String.format("%.2f €", totalDeProductos(productos)),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                if (modoCompra && rojos.isNotEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 4.dp)
                                .background(RedCompradoDark, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("Comprados", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    items(rojos, key = { "rojo-${it.id ?: 0}" }) { p ->
                        ItemCard(
                            producto = p,
                            seleccionado = false,
                            enRojo = true,
                            mostrarPrecio = lista?.mostrarPrecios == true,
                            onTap = { p.id?.let { id -> comprados = comprados - id } },
                            onLong = { },
                            onPrecioClick = { }
                        )
                    }

                    if (lista?.mostrarPrecios == true) {
                        item {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Total comprado:",
                                    color = RedComprado,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    String.format("%.2f €", totalDeProductos(rojos)),
                                    color = RedComprado,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Botón "volver" inferior izquierdo (lado opuesto al "+")
        BackBoton(onClick = onBack, modifier = Modifier.align(Alignment.BottomStart))

        // FAB inferior derecho — "+" en modo normal, "Comprar" en modo compra
        if (modoCompra) {
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
                    .background(GreenAccent, RoundedCornerShape(28.dp))
                    .clickable(enabled = comprados.isNotEmpty()) {
                        // captura una copia y elimina secuencialmente, recargando al final
                        val aEliminar = comprados.toSet()
                        vm.eliminarVarios(aEliminar) {
                            comprados = comprados - aEliminar
                            if (comprados.isEmpty()) modoCompra = false
                        }
                    }
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Comprar",
                    color = if (comprados.isNotEmpty()) Color.Black else Color.Black.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
                    .size(56.dp)
                    .background(GreenAccent, CircleShape)
                    .clickable { onAnadirItem() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, null, tint = Color.Black, modifier = Modifier.size(28.dp))
            }
        }
    }

    if (showCompartir) {
        ModalBottomSheet(onDismissRequest = { showCompartir = false }, containerColor = GreenMedium) {
            CompartirSheet(onEnviar = { tag ->
                vm.compartirCon(
                    tag = tag,
                    onDone = { showCompartir = false },
                    onError = { /* mostrar */ }
                )
            })
        }
    }
    if (showOpciones && lista != null) {
        ModalBottomSheet(onDismissRequest = { showOpciones = false }, containerColor = GreenMedium) {
            OpcionesSheet(
                lista = lista,
                onGuardar = { nombre, asc, mostrar ->
                    vm.renombrar(nombre)
                    vm.configurar(asc, mostrar) { showOpciones = false }
                }
            )
        }
    }
    if (showOpcionesSeleccion) {
        ModalBottomSheet(onDismissRequest = { showOpcionesSeleccion = false }, containerColor = GreenMedium) {
            MantenerSheet(
                onCopiar = { showOpcionesSeleccion = false; showCopiar = true },
                onEliminar = { vm.eliminarSeleccionados(); showOpcionesSeleccion = false }
            )
        }
    }
    if (showCopiar) {
        ModalBottomSheet(onDismissRequest = { showCopiar = false }, containerColor = GreenMedium) {
            CopiarASheet(
                excluirId = listaId,
                onCopiarA = { idDest ->
                    vm.copiarSeleccionadosA(idDest) { showCopiar = false }
                }
            )
        }
    }
    itemPulsado?.let { p ->
        ModalBottomSheet(onDismissRequest = { itemPulsado = null }, containerColor = GreenMedium) {
            PulsarItemSheet(
                producto = p,
                onGuardar = { nuevo ->
                    p.id?.let { vm.editarItem(it, nuevo) { itemPulsado = null } }
                }
            )
        }
    }
    precioItem?.let { p ->
        ModalBottomSheet(onDismissRequest = { precioItem = null }, containerColor = GreenMedium) {
            ClickPrecioSheet(
                inicial = p.precio,
                onGuardar = { nuevo ->
                    p.id?.let { vm.editarItem(it, p.copy(precio = nuevo)) { precioItem = null } }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemCard(
    producto: ProductoListaDTO,
    seleccionado: Boolean,
    enRojo: Boolean = false,
    mostrarPrecio: Boolean,
    onTap: () -> Unit,
    onLong: () -> Unit,
    onPrecioClick: () -> Unit
) {
    val color = when {
        enRojo -> RedComprado
        seleccionado -> GreenDark
        else -> GreenMedium
    }
    val precioBg = if (enRojo) RedCompradoDark else GreenDark
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color, RoundedCornerShape(12.dp))
            .pointerInput(producto.id) {
                detectTapGestures(
                    onTap = { onTap() },
                    onLongPress = { onLong() }
                )
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(producto.nombre, color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(16.dp))
        producto.cantidad?.takeIf { it.isNotBlank() }?.let {
            Text(it, color = Color.White)
            Spacer(Modifier.width(4.dp))
            producto.unidadMedida?.takeIf { u -> u.isNotBlank() }?.let { u ->
                Text(u, color = Color.White)
            }
        }
        Spacer(Modifier.weight(1f))
        if (mostrarPrecio) {
            Text(
                String.format("%.2f €", subtotalDe(producto)),
                color = Color.White,
                modifier = Modifier
                    .background(precioBg, RoundedCornerShape(8.dp))
                    .clickable { onPrecioClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun ModoCompraToggle(activo: Boolean, onClick: () -> Unit) {
    val bg = if (activo) GreenAccent else GreenMedium
    val txt = if (activo) Color.Black else Color.White
    Box(
        Modifier
            .background(bg, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Modo compra", color = txt, fontWeight = FontWeight.SemiBold)
    }
}

private fun subtotalDe(p: ProductoListaDTO): Double {
    val precio = p.precio ?: 0.0
    val cant = p.cantidad?.replace(",", ".")?.toDoubleOrNull() ?: 1.0
    val mult = if (cant <= 0.0) 1.0 else cant
    return precio * mult
}

internal fun totalDeProductos(productos: List<ProductoListaDTO>): Double =
    productos.sumOf { subtotalDe(it) }

// ====== Sheets ======

@Composable
fun CompartirSheet(onEnviar: (String) -> Unit) {
    var tag by remember { mutableStateOf("") }

    // Carga la lista de amigos del usuario para poder invitar con un click.
    val amigosVm: com.example.tfg.ui.viewmodel.AmigosViewModel = viewModel()
    val amigosState by amigosVm.state.collectAsState()
    LaunchedEffect(Unit) { amigosVm.cargar() }

    Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
        Text("Compartir lista", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        if (amigosState.amigos.isNotEmpty()) {
            Text("Tus amigos", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 220.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(amigosState.amigos, key = { it.id }) { amigo ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(GreenDark, RoundedCornerShape(10.dp))
                            .clickable {
                                amigo.tagAmigo?.let { t -> if (t.isNotBlank()) onEnviar(t) }
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(amigo.nombreUsuario ?: "?", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.weight(1f))
                        Text(
                            amigo.tagAmigo ?: "",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "O introduce un tag manualmente:",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(8.dp))
        }

        GreenTextField(value = tag, onValueChange = { tag = it.uppercase() },
            placeholder = "Tag de amigo (8 caracteres)", background = GreenDark)
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            PrimaryButton("Enviar", { if (tag.isNotBlank()) onEnviar(tag.trim()) }, enabled = tag.length in 1..16)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun OpcionesSheet(
    lista: ListaDTO,
    onGuardar: (nombre: String, asc: Boolean, mostrar: Boolean) -> Unit
) {
    var nombre by remember { mutableStateOf(lista.nombre) }
    var asc by remember { mutableStateOf(lista.ordenAscendente ?: true) }
    var mostrar by remember { mutableStateOf(lista.mostrarPrecios ?: false) }

    Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
        GreenTextField(value = nombre, onValueChange = { nombre = it },
            placeholder = "Cambiar nombre de la lista", background = GreenDark)
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .background(GreenDark, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) { Text("Ordenado por:", color = Color.White) }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth().padding(start = 80.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ToggleBtn("Ascendente", asc, { asc = true }, Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth().padding(start = 80.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ToggleBtn("Descendente", !asc, { asc = false }, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        ToggleBtn("Mostrar precios", mostrar, { mostrar = !mostrar }, Modifier.fillMaxWidth())
        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            PrimaryButton("Guardar", { onGuardar(nombre, asc, mostrar) })
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ToggleBtn(text: String, sel: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(if (sel) GreenDark else GreenMedium, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White)
    }
}

@Composable
fun MantenerSheet(onCopiar: () -> Unit, onEliminar: () -> Unit) {
    Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PrimaryButton("Copiar", onCopiar, modifier = Modifier.fillMaxWidth())
        PrimaryButton("Eliminar", onEliminar, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun CopiarASheet(excluirId: Long, onCopiarA: (Long) -> Unit) {
    val listasVm: ListasViewModel = viewModel()
    val st by listasVm.state.collectAsState()
    LaunchedEffect(Unit) { listasVm.cargar() }

    Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
        Text("Copiar a:", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        st.listas.filter { it.id != excluirId }.forEach { l ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(GreenDark, RoundedCornerShape(10.dp))
                    .clickable { l.id?.let(onCopiarA) }
                    .padding(16.dp)
            ) {
                Text(l.nombre, color = Color.White)
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
fun PulsarItemSheet(producto: ProductoListaDTO, onGuardar: (ProductoListaDTO) -> Unit) {
    var nombre by remember { mutableStateOf(producto.nombre) }
    var unidad by remember { mutableStateOf(producto.unidadMedida ?: "") }
    var cantidad by remember { mutableStateOf(producto.cantidad ?: "") }
    var precio by remember { mutableStateOf(producto.precio?.let { if (it == 0.0) "" else it.toString() } ?: "") }

    fun setUnidad(u: String) { unidad = u }
    fun cantidadNum() = cantidad.toIntOrNull() ?: 0

    Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
        // Fila 1: nombre (mitad izquierda) | precio (mitad derecha)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.weight(1f)) {
                GreenTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    placeholder = "Nombre",
                    background = GreenDark
                )
            }
            Box(Modifier.weight(1f)) {
                OutlinedTextField(
                    value = precio,
                    onValueChange = { v ->
                        if (v.matches(Regex("^\\d*(\\.\\d{0,2})?\$"))) precio = v
                    },
                    placeholder = { Text("Precio", color = Color.White.copy(alpha = 0.7f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().background(GreenDark, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedContainerColor = GreenDark, unfocusedContainerColor = GreenDark,
                        focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color.White
                    )
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        // Fila 2: unidad | cantidad (con sus botones debajo)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f)) {
                GreenTextField(value = unidad, onValueChange = { unidad = it }, placeholder = "Unidad", background = GreenDark)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Uds", "Kg", "G").forEach { u ->
                        Box(
                            Modifier
                                .weight(1f)
                                .background(GreenAccent, RoundedCornerShape(8.dp))
                                .clickable { setUnidad(u) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) { Text(u, color = Color.Black) }
                    }
                }
            }
            Column(Modifier.weight(1f)) {
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { v -> if (v.all { it.isDigit() }) cantidad = v },
                    placeholder = { Text("Cantidad", color = Color.White.copy(alpha = 0.7f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().background(GreenDark, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedContainerColor = GreenDark, unfocusedContainerColor = GreenDark,
                        focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color.White
                    )
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        Modifier
                            .weight(1f)
                            .background(GreenAccent, RoundedCornerShape(8.dp))
                            .clickable { cantidad = (cantidadNum() + 1).toString() }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("+", color = Color.Black, fontWeight = FontWeight.Bold) }
                    Box(
                        Modifier
                            .weight(1f)
                            .background(GreenAccent, RoundedCornerShape(8.dp))
                            .clickable {
                                val n = cantidadNum() - 1
                                if (n >= 0) cantidad = if (n == 0) "" else n.toString()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("-", color = Color.Black, fontWeight = FontWeight.Bold) }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            PrimaryButton("Guardar", {
                onGuardar(producto.copy(
                    nombre = nombre,
                    cantidad = cantidad.ifBlank { null },
                    unidadMedida = unidad.ifBlank { null },
                    precio = precio.toDoubleOrNull()
                ))
            })
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun ClickPrecioSheet(inicial: Double?, onGuardar: (Double) -> Unit) {
    var precio by remember { mutableStateOf(inicial?.toString() ?: "") }
    Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
        OutlinedTextField(
            value = precio,
            onValueChange = { v ->
                if (v.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) precio = v
            },
            placeholder = { Text("Introduzca precio", color = Color.White.copy(alpha = 0.7f)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().background(GreenDark, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                focusedContainerColor = GreenDark, unfocusedContainerColor = GreenDark,
                focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.White
            )
        )
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            PrimaryButton("Guardar", {
                precio.toDoubleOrNull()?.let(onGuardar)
            })
        }
        Spacer(Modifier.height(8.dp))
    }
}
