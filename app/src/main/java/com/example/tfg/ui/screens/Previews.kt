package com.example.tfg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfg.ui.components.BottomBar
import com.example.tfg.ui.components.BottomTab
import com.example.tfg.ui.components.GreenTextField
import com.example.tfg.ui.components.PrimaryButton
import com.example.tfg.ui.components.ProfileAvatar
import com.example.tfg.ui.theme.GrayInactive
import com.example.tfg.ui.theme.GreenAccent
import com.example.tfg.ui.theme.GreenDark
import com.example.tfg.ui.theme.GreenMedium
import com.example.tfg.ui.theme.TFGTheme

private const val DEVICE = "spec:width=411dp,height=891dp"

// ===================== 00 - LOGIN =====================

@Preview(name = "00 - Login", device = DEVICE, showBackground = true)
@Composable
private fun PreviewLogin() {
    TFGTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Logo()
                Spacer(Modifier.height(24.dp))
                Text("Bienvenido a BuyNotes", color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))
                GreenTextField("usuario", {}, "Usuario o correo")
                Spacer(Modifier.height(12.dp))
                GreenTextField("", {}, "Contraseña", isPassword = true)
                Spacer(Modifier.height(8.dp))
                Box(Modifier.fillMaxWidth()) {
                    Text("He olvidado mi contraseña", color = MaterialTheme.colorScheme.onBackground,
                        textDecoration = TextDecoration.Underline, fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.CenterStart))
                }
                Spacer(Modifier.height(28.dp))
                OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth(0.75f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)) {
                    Text("Continuar con Google")
                }
                Spacer(Modifier.height(28.dp))
                PrimaryButton("Iniciar Sesión", {}, Modifier.fillMaxWidth(0.5f))
                Spacer(Modifier.height(24.dp))
                Text("No tengo cuenta, registrarme", color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = TextDecoration.Underline, fontSize = 14.sp)
            }
        }
    }
}

// ===================== 01 - REGISTRO =====================

@Preview(name = "01 - Registro", device = DEVICE, showBackground = true)
@Composable
private fun PreviewRegistro() {
    TFGTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Logo()
                Spacer(Modifier.height(20.dp))
                Text("Bienvenido a BuyNotes", color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(24.dp))
                GreenTextField("Jorge", {}, "Nombre")
                Spacer(Modifier.height(10.dp))
                GreenTextField("jperez", {}, "Usuario")
                Spacer(Modifier.height(10.dp))
                GreenTextField("jorge@ej.com", {}, "Correo")
                Spacer(Modifier.height(10.dp))
                GreenTextField("", {}, "Contraseña", isPassword = true)
                Spacer(Modifier.height(24.dp))
                Row(
                    Modifier.fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(6.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(28.dp).background(GreenAccent, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Check, null, tint = Color.Black) }
                    Spacer(Modifier.width(12.dp))
                    Text("No soy un robot", color = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(Modifier.height(28.dp))
                PrimaryButton("Crear cuenta", {}, Modifier.fillMaxWidth(0.6f))
                Spacer(Modifier.height(12.dp))
                Text("Volver", color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

// ===================== 02 - PANTALLA GENERAL =====================

@Preview(name = "02 - PantallaGeneral", device = DEVICE, showBackground = true)
@Composable
private fun PreviewPantallaGeneral() {
    TFGTheme {
        Scaffold(bottomBar = { BottomBar(BottomTab.LISTAS) {} },
            containerColor = MaterialTheme.colorScheme.background) { p ->
            Column(
                Modifier.padding(p).fillMaxSize().padding(horizontal = 12.dp, vertical = 56.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Listas de compra", color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Box(Modifier.size(44.dp).background(GreenAccent, CircleShape),
                        contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, null, tint = Color.Black)
                    }
                }
                Spacer(Modifier.height(40.dp))
                listOf("Compra semanal", "Lista del finde", "Cumpleaños").forEach {
                    ListaCard(it) {}
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Preview(name = "08 - AñadirListaNueva (sheet)", device = DEVICE, showBackground = true)
@Composable
private fun PreviewAddListaSheet() {
    TFGTheme {
        Surface(color = GreenMedium, modifier = Modifier.fillMaxWidth()) {
            AnadirListaSheet(onCrear = {})
        }
    }
}

// ===================== 03 - DENTRO LISTA =====================

@Preview(name = "03 - DentroLista", device = DEVICE, showBackground = true)
@Composable
private fun PreviewDentroLista() {
    TFGTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 56.dp)) {
                Column(Modifier.fillMaxSize()) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Compra semanal", color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Icon(Icons.Default.PersonAdd, null,
                            tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(12.dp))
                        Icon(Icons.Default.MoreVert, null,
                            tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    listOf(
                        Triple("Leche", "2 litros", 0.89),
                        Triple("Pan de molde", "1 paquete", 1.50),
                        Triple("Plátanos", "1 kg", 1.20)
                    ).forEach { (n, c, p) ->
                        ItemCardPreview(n, c, p, false)
                        Spacer(Modifier.height(8.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total:", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                        Text("3.59 €", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                    }
                }
                Box(Modifier.align(Alignment.BottomStart).padding(bottom = 16.dp)
                    .size(56.dp).background(GreenAccent, CircleShape),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, null, tint = Color.Black, modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}

@Composable
private fun ItemCardPreview(nombre: String, cantidad: String, precio: Double, sel: Boolean) {
    val color = if (sel) GreenDark else GreenMedium
    Row(
        Modifier.fillMaxWidth().background(color, RoundedCornerShape(12.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(nombre, color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(16.dp))
        Text(cantidad, color = Color.White)
        Spacer(Modifier.weight(1f))
        Text(String.format("%.2f €", precio), color = Color.White,
            modifier = Modifier.background(GreenDark, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp))
    }
}

// ===================== 04 - COMPARTIR LISTA (sheet) =====================

@Preview(name = "04 - CompartirLista (sheet)", device = DEVICE, showBackground = true)
@Composable
private fun PreviewCompartir() {
    TFGTheme {
        Surface(color = GreenMedium, modifier = Modifier.fillMaxWidth()) {
            CompartirSheet(onEnviar = {})
        }
    }
}

// ===================== 05 - OPCIONES DENTRO LISTA (sheet) =====================

@Preview(name = "05 - OpcionesDentroLista (sheet)", device = DEVICE, showBackground = true)
@Composable
private fun PreviewOpciones() {
    TFGTheme {
        Surface(color = GreenMedium, modifier = Modifier.fillMaxWidth()) {
            OpcionesSheet(
                lista = com.example.tfg.data.model.ListaDTO(
                    id = 1, nombre = "Compra semanal",
                    ordenAscendente = true, mostrarPrecios = true
                ),
                onGuardar = { _, _, _ -> }
            )
        }
    }
}

// ===================== 06 - AÑADIR ITEM PREDEFINIDO =====================

@Preview(name = "06 - AñadirItem Predefinido", device = DEVICE, showBackground = true)
@Composable
private fun PreviewAddItemPredefinido() {
    TFGTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 56.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.width(12.dp))
                    Text("Añadir producto", color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth()) {
                    TabHeaderPreview("Predefinidos", true, Modifier.weight(1f))
                    TabHeaderPreview("Añadir uno nuevo", false, Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))
                Column(Modifier.padding(start = 24.dp)) {
                    HeaderPreview("Favoritos", true)
                    Spacer(Modifier.height(4.dp))
                    listOf("Leche entera 2 litros", "Yogur natural").forEach {
                        ItemRowPreview(it); Spacer(Modifier.height(4.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    HeaderPreview("Sistema", false)
                }
            }
        }
    }
}

@Composable
private fun TabHeaderPreview(text: String, sel: Boolean, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text, color = MaterialTheme.colorScheme.onBackground,
            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
        Spacer(Modifier.height(6.dp))
        Box(Modifier.height(3.dp).fillMaxWidth().background(if (sel) GreenDark else Color.Transparent))
    }
}

@Composable
private fun HeaderPreview(label: String, open: Boolean) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Icon(if (open) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
            null, tint = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun ItemRowPreview(text: String) {
    Box(
        Modifier.fillMaxWidth().padding(start = 32.dp)
            .background(GreenMedium, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) { Text(text, color = Color.White) }
}

// ===================== 07 - AÑADIR ITEM NUEVO =====================

@Preview(name = "07 - AñadirItem Nuevo", device = DEVICE, showBackground = true)
@Composable
private fun PreviewAddItemNuevo() {
    TFGTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 56.dp)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.width(12.dp))
                        Text("Añadir producto", color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth()) {
                        TabHeaderPreview("Predefinidos", false, Modifier.weight(1f))
                        TabHeaderPreview("Añadir uno nuevo", true, Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CardField("Introduzca el nombre", "Pan de molde", {})
                        CardField("Introduzca la cantidad", "1", {})
                        CardField("Unidad de medida del producto", "paquete", {})
                        CardField("Introduce el precio", "1.50", {})
                    }
                }
                Box(Modifier.align(Alignment.BottomEnd).padding(16.dp).size(56.dp)
                    .background(GreenAccent, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, null, tint = Color.Black)
                }
            }
        }
    }
}

// ===================== 09 - PULSAR ITEM (sheet) =====================

@Preview(name = "09 - PulsarItem (sheet)", device = DEVICE, showBackground = true)
@Composable
private fun PreviewPulsarItem() {
    TFGTheme {
        Surface(color = GreenMedium, modifier = Modifier.fillMaxWidth()) {
            PulsarItemSheet(
                producto = com.example.tfg.data.model.ProductoListaDTO(
                    id = 1, nombre = "Leche", cantidad = "2", unidadMedida = "litros", precio = 0.89
                ),
                onGuardar = {}
            )
        }
    }
}

// ===================== 10 - MANTENER 3 PUNTOS (sheet) =====================

@Preview(name = "10 - MantenerItem3Puntos (sheet)", device = DEVICE, showBackground = true)
@Composable
private fun PreviewMantener() {
    TFGTheme {
        Surface(color = GreenMedium, modifier = Modifier.fillMaxWidth()) {
            MantenerSheet(onCopiar = {}, onEliminar = {})
        }
    }
}

// ===================== 11 - MENU COPIAR (sheet) =====================

@Preview(name = "11 - MenuCopiar (sheet)", device = DEVICE, showBackground = true)
@Composable
private fun PreviewCopiar() {
    TFGTheme {
        Surface(color = GreenMedium, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Copiar a:", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                listOf("Lista del finde", "Cumpleaños").forEach {
                    Box(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            .background(GreenDark, RoundedCornerShape(10.dp)).padding(16.dp)
                    ) { Text(it, color = Color.White) }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

// ===================== 12 - CLICK PRECIO (sheet) =====================

@Preview(name = "12 - ClickPrecio (sheet)", device = DEVICE, showBackground = true)
@Composable
private fun PreviewClickPrecio() {
    TFGTheme {
        Surface(color = GreenMedium, modifier = Modifier.fillMaxWidth()) {
            ClickPrecioSheet(inicial = 0.89, onGuardar = {})
        }
    }
}

// ===================== 13 - FAVORITOS =====================

@Preview(name = "13 - Favoritos", device = DEVICE, showBackground = true)
@Composable
private fun PreviewFavoritos() {
    TFGTheme {
        Scaffold(bottomBar = { BottomBar(BottomTab.FAVORITOS) {} },
            containerColor = MaterialTheme.colorScheme.background) { p ->
            Column(Modifier.padding(p).fillMaxSize().padding(horizontal = 12.dp, vertical = 56.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Items favoritos", color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Box(Modifier.size(44.dp).background(GreenAccent, CircleShape),
                        contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, null, tint = Color.Black)
                    }
                }
                Spacer(Modifier.height(16.dp))
                listOf(
                    Triple("Leche entera", "2 litros", 0.89),
                    Triple("Yogur natural", "4 unidades", 0.30)
                ).forEach { (n, c, p) ->
                    ItemCardPreview(n, c, p, false)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

// ===================== 14 - AÑADIR ITEM FAVORITO =====================

@Preview(name = "14 - AñadirItemFavorito", device = DEVICE, showBackground = true)
@Composable
private fun PreviewAddFavorito() {
    TFGTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 56.dp)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.width(12.dp))
                        Text("Crear un nuevo ítem favorito", color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(28.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
                        CardField("Introduzca el nombre", "", {})
                        CardField("Introduzca la cantidad", "", {})
                        CardField("Unidad de medida del producto", "", {})
                        CardField("Introduce el precio", "", {})
                    }
                }
                Box(Modifier.align(Alignment.BottomEnd).padding(16.dp).size(56.dp)
                    .background(GreenAccent, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, null, tint = Color.Black)
                }
            }
        }
    }
}

// ===================== 15 - PERFIL =====================

@Preview(name = "15 - Perfil", device = DEVICE, showBackground = true)
@Composable
private fun PreviewPerfil() {
    TFGTheme {
        Scaffold(bottomBar = { BottomBar(BottomTab.PERFIL) {} },
            containerColor = MaterialTheme.colorScheme.background) { p ->
            Column(Modifier.padding(p).fillMaxSize().padding(horizontal = 12.dp, vertical = 56.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Perfil", color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    ProfileAvatar("J", 48)
                }
                Spacer(Modifier.height(24.dp))
                TemaOscuroPreview(true)
                Spacer(Modifier.height(12.dp))
                listOf("Notificaciones", "Mis amigos", "Cambiar nombre", "Cambiar correo",
                    "Cambiar contraseña", "Panel de Administración").forEach {
                    ProfileButtonPreview(it)
                    Spacer(Modifier.height(12.dp))
                }
                Spacer(Modifier.weight(1f))
                PrimaryButton("Cerrar sesión", {}, Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TemaOscuroPreview(activado: Boolean) {
    Row(
        Modifier.fillMaxWidth().background(GreenMedium, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Tema oscuro", color = Color.White, modifier = Modifier.weight(1f))
        Switch(checked = activado, onCheckedChange = {},
            colors = SwitchDefaults.colors(
                checkedThumbColor = GreenAccent, uncheckedThumbColor = GrayInactive,
                checkedTrackColor = GreenDark, uncheckedTrackColor = Color.DarkGray
            ))
    }
}

@Composable
private fun ProfileButtonPreview(text: String) {
    Box(
        Modifier.fillMaxWidth().background(GreenMedium, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) { Text(text, color = Color.White, fontSize = 16.sp) }
}

// ===================== 16 - CAMBIAR NOMBRE (sheet) =====================

@Preview(name = "16 - CambiarNombre (sheet)", device = DEVICE, showBackground = true)
@Composable
private fun PreviewCambiarNombre() {
    TFGTheme {
        Surface(color = GreenMedium, modifier = Modifier.fillMaxWidth()) {
            CambiarNombreSheet(onGuardar = {})
        }
    }
}

// ===================== 17 - CAMBIAR CORREO =====================

@Preview(name = "17 - CambiarCorreo", device = DEVICE, showBackground = true)
@Composable
private fun PreviewCambiarCorreo() {
    TFGTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Volver", color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Start))
                Spacer(Modifier.height(99.dp))
                Text("Introduzca su nuevo correo electrónico",
                    color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
                Spacer(Modifier.height(99.dp))
                GreenTextField("", {}, "Correo")
                Spacer(Modifier.height(99.dp))
                PrimaryButton("Enviar", {})
            }
        }
    }
}

// ===================== 18 - CAMBIAR CONTRASEÑA =====================

@Preview(name = "18 - CambiarContraseña", device = DEVICE, showBackground = true)
@Composable
private fun PreviewCambiarContrasena() {
    TFGTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 56.dp)) {
                Text("Volver", color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(24.dp))
                Text("Cambiar contraseña", color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(24.dp))
                GreenTextField("", {}, "Contraseña actual", isPassword = true)
                Spacer(Modifier.height(12.dp))
                GreenTextField("", {}, "Nueva contraseña", isPassword = true)
                Spacer(Modifier.height(12.dp))
                GreenTextField("", {}, "Repetir nueva contraseña", isPassword = true)
                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    PrimaryButton("Guardar", {})
                }
            }
        }
    }
}

// ===================== 19 - PANEL ADMIN =====================

@Preview(name = "19 - Panel Admin", device = DEVICE, showBackground = true)
@Composable
private fun PreviewAdmin() {
    TFGTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 56.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.width(12.dp))
                    Text("Panel de Administración", color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(24.dp))
                GreenTextField("", {}, "Buscar por usuario, tag o correo...", background = GreenDark)
                Spacer(Modifier.height(16.dp))
                listOf(
                    Triple("jperez", "jorge@ej.com", "ADMIN" to "AB12CD34"),
                    Triple("maria_g", "maria@ej.com", "USER" to "XY98ZW12")
                ).forEach { (nu, em, rt) ->
                    Row(
                        Modifier.fillMaxWidth().background(GreenMedium, RoundedCornerShape(12.dp)).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(nu, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(em, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(rt.first, color = if (rt.first == "ADMIN") GreenAccent else Color.White,
                                fontWeight = FontWeight.Bold)
                            Text(rt.second, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

// ===================== 20 - OPCIONES ADMIN USUARIO (sheet) =====================

@Preview(name = "20 - Opciones Admin (sheet)", device = DEVICE, showBackground = true)
@Composable
private fun PreviewOpcionesAdmin() {
    TFGTheme {
        Surface(color = GreenMedium, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Gestionando a: maria_g", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                PrimaryButton("Hacer Administrador", {}, Modifier.fillMaxWidth())
                PrimaryButton("Eliminar Usuario", {}, Modifier.fillMaxWidth())
                PrimaryButton("Cerrar", {}, Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ===================== NOTIFICACIONES =====================

@Preview(name = "21 - Notificaciones", device = DEVICE, showBackground = true)
@Composable
private fun PreviewNotificaciones() {
    TFGTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 56.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.width(12.dp))
                    Text("Notificaciones", color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
                Text("Solicitudes de amistad", color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                NotifCardPreview("maria_g", "XY98ZW12")
                Spacer(Modifier.height(16.dp))
                Text("Invitaciones a listas", color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                NotifCardPreview("Lista del finde", "Invita: jperez")
            }
        }
    }
}

@Composable
private fun NotifCardPreview(top: String, bottom: String) {
    Row(
        Modifier.fillMaxWidth().background(GreenMedium, RoundedCornerShape(12.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(top, color = Color.White, fontWeight = FontWeight.Bold)
            Text(bottom, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
        }
        Box(Modifier.size(40.dp).background(GreenAccent, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Check, null, tint = Color.Black)
        }
        Spacer(Modifier.width(8.dp))
        Box(Modifier.size(40.dp).background(GreenDark, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Close, null, tint = Color.White)
        }
    }
}

// ===================== AMIGOS =====================

@Preview(name = "22 - Mis amigos", device = DEVICE, showBackground = true)
@Composable
private fun PreviewAmigos() {
    TFGTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 56.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.width(12.dp))
                    Text("Mis amigos", color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Box(Modifier.size(44.dp).background(GreenAccent, CircleShape),
                        contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, null, tint = Color.Black)
                    }
                }
                Spacer(Modifier.height(16.dp))
                listOf("maria_g" to "XY98ZW12", "carlos_m" to "PQ34RS56").forEach { (nu, tag) ->
                    Row(
                        Modifier.fillMaxWidth().background(GreenMedium, RoundedCornerShape(12.dp)).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(nu, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(tag, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                        }
                        Box(Modifier.size(40.dp).background(GreenDark, CircleShape),
                            contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Delete, null, tint = Color.White)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
