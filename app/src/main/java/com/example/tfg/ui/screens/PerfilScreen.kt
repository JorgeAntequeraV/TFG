package com.example.tfg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.tfg.ui.components.BottomBar
import com.example.tfg.ui.components.BottomTab
import com.example.tfg.ui.components.GreenTextField
import com.example.tfg.ui.components.PrimaryButton
import com.example.tfg.ui.components.ProfileAvatar
import com.example.tfg.ui.theme.GrayInactive
import com.example.tfg.ui.theme.GreenAccent
import com.example.tfg.ui.theme.GreenDark
import com.example.tfg.ui.theme.GreenMedium
import com.example.tfg.ui.viewmodel.PerfilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onCambiarCorreo: () -> Unit,
    onCambiarContrasena: () -> Unit,
    onAdmin: () -> Unit,
    onAmigos: () -> Unit,
    onNotificaciones: () -> Unit,
    onLogout: () -> Unit,
    onTabChange: (BottomTab) -> Unit
) {
    val vm: PerfilViewModel = viewModel()
    val sessionData by vm.sessionState.collectAsState()
    val cuenta by vm.cuenta.collectAsState()
    val perfilState by vm.state.collectAsState()
    val toast = com.example.tfg.ui.components.rememberToast()
    var showCambiarNombre by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.cargarCuenta() }
    LaunchedEffect(perfilState.mensaje) { perfilState.mensaje?.let { toast.exito(it); vm.limpiar() } }
    LaunchedEffect(perfilState.error) { perfilState.error?.let { toast.error(it); vm.limpiar() } }

    val config = LocalConfiguration.current
    val padTop = config.screenHeightDp.dp / 16
    val padLR = config.screenWidthDp.dp / 32

    val initial = sessionData.nombreUsuario?.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    Scaffold(
        bottomBar = { BottomBar(BottomTab.PERFIL, onTabChange) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0)
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = padLR)
                .padding(top = padTop)
        ) {
            Row(
                Modifier.fillMaxWidth().height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Perfil", color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                ProfileAvatar(initial = initial, size = 48)
            }
            Spacer(Modifier.height(24.dp))

            TemaOscuroCard(
                activado = sessionData.temaOscuro,
                onToggle = { vm.toggleTemaOscuro(it) }
            )
            Spacer(Modifier.height(12.dp))
            CuentaCard(cuenta)
            Spacer(Modifier.height(12.dp))
            ProfileButton("Notificaciones") { onNotificaciones() }
            Spacer(Modifier.height(12.dp))
            ProfileButton("Mis amigos") { onAmigos() }
            Spacer(Modifier.height(12.dp))
            ProfileButton("Cambiar nombre") { showCambiarNombre = true }
            Spacer(Modifier.height(12.dp))
            ProfileButton("Cambiar correo") { onCambiarCorreo() }
            Spacer(Modifier.height(12.dp))
            ProfileButton("Cambiar contraseña") { onCambiarContrasena() }

            if (sessionData.rol == "ROLE_ADMIN") {
                Spacer(Modifier.height(12.dp))
                ProfileButton("Panel de Administración") { onAdmin() }
            }

            Spacer(Modifier.height(24.dp))
            PrimaryButton(
                text = "Cerrar sesión",
                onClick = { vm.logout(onLogout) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }
    }

    if (showCambiarNombre) {
        ModalBottomSheet(onDismissRequest = { showCambiarNombre = false }, containerColor = GreenMedium) {
            CambiarNombreSheet(
                onGuardar = { nuevo -> vm.cambiarNombreUsuario(nuevo) { showCambiarNombre = false } }
            )
        }
    }
}

@Composable
private fun TemaOscuroCard(activado: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(GreenMedium, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Tema oscuro", color = Color.White, modifier = Modifier.weight(1f), fontSize = 16.sp)
        Switch(
            checked = activado,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GreenAccent,
                uncheckedThumbColor = GrayInactive,
                checkedTrackColor = GreenDark,
                uncheckedTrackColor = Color.DarkGray
            )
        )
    }
}

@Composable
private fun CuentaCard(cuenta: com.example.tfg.ui.viewmodel.CuentaInfo) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(GreenMedium, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text("Cuenta", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        CuentaRow("Nombre", cuenta.nombre)
        CuentaRow("Usuario", cuenta.nombreUsuario)
        CuentaRow("Correo", cuenta.email)
    }
}

@Composable
private fun CuentaRow(etiqueta: String, valor: String?) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(etiqueta, color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp,
            modifier = Modifier.width(80.dp))
        Text(
            valor?.takeIf { it.isNotBlank() } ?: "…",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ProfileButton(text: String, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(GreenMedium, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text, color = Color.White, fontSize = 16.sp)
    }
}

@Composable
fun CambiarNombreSheet(onGuardar: (String) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
        Text("Cambiar nombre", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        GreenTextField(
            value = nombre,
            onValueChange = { nombre = it },
            placeholder = "Introduzca un nuevo nombre",
            background = GreenDark
        )
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            PrimaryButton("Guardar", { if (nombre.isNotBlank()) onGuardar(nombre.trim()) },
                enabled = nombre.isNotBlank())
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun CambiarCorreoScreen(onBack: () -> Unit) {
    val vm: PerfilViewModel = viewModel()
    val st by vm.state.collectAsState()
    val toast = com.example.tfg.ui.components.rememberToast()
    var correo by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(st.mensaje) { st.mensaje?.let { toast.exito(it); vm.limpiar() } }
    LaunchedEffect(st.error) { st.error?.let { toast.error(it); vm.limpiar() } }

    val config = LocalConfiguration.current
    val padLR = config.screenWidthDp.dp / 32
    val padTop = config.screenHeightDp.dp / 16
    val gap = config.screenHeightDp.dp / 9

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = padLR)
                .padding(top = padTop),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(gap))
            Text(
                "Introduzca su nuevo correo electrónico",
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(gap))
            GreenTextField(
                value = correo,
                onValueChange = { correo = it },
                placeholder = "Correo",
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(gap))
            PrimaryButton("Enviar", {
                localError = null
                if (!correo.contains("@") || !correo.substringAfter("@").contains(".")) {
                    localError = "Correo inválido"
                } else {
                    vm.cambiarCorreo(correo.trim()) { onBack() }
                }
            }, enabled = correo.isNotBlank())
            Spacer(Modifier.height(12.dp))
            (localError ?: st.error)?.let { Text(it, color = Color.Red) }
            st.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.onBackground) }
        }
        com.example.tfg.ui.components.BackBoton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(start = padLR)
        )
    }
}

@Composable
fun CambiarContrasenaScreen(onBack: () -> Unit) {
    val vm: PerfilViewModel = viewModel()
    val st by vm.state.collectAsState()
    val toast = com.example.tfg.ui.components.rememberToast()
    var actual by remember { mutableStateOf("") }
    var nueva by remember { mutableStateOf("") }
    var nuevaRep by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(st.mensaje) { st.mensaje?.let { toast.exito(it); vm.limpiar() } }
    LaunchedEffect(st.error) { st.error?.let { toast.error(it); vm.limpiar() } }

    val config = LocalConfiguration.current
    val padLR = config.screenWidthDp.dp / 32
    val padTop = config.screenHeightDp.dp / 16

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = padLR)
                .padding(top = padTop)
        ) {
            Text("Cambiar contraseña", color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))
            GreenTextField(actual, { actual = it }, "Contraseña actual", isPassword = true)
            Spacer(Modifier.height(12.dp))
            GreenTextField(nueva, { nueva = it }, "Nueva contraseña", isPassword = true)
            Spacer(Modifier.height(12.dp))
            GreenTextField(nuevaRep, { nuevaRep = it }, "Repetir nueva contraseña", isPassword = true)
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                PrimaryButton("Guardar", {
                    localError = null
                    if (nueva != nuevaRep) localError = "Las contraseñas no coinciden"
                    else if (nueva.contains(" ")) localError = "La contraseña no puede tener espacios"
                    else if (nueva.length < 4) localError = "Contraseña demasiado corta"
                    else vm.cambiarContrasena(actual, nueva) { onBack() }
                }, enabled = actual.isNotBlank() && nueva.isNotBlank() && nuevaRep.isNotBlank())
            }
            Spacer(Modifier.height(12.dp))
            (localError ?: st.error)?.let { Text(it, color = Color.Red) }
            st.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.onBackground) }
        }
        com.example.tfg.ui.components.BackBoton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(start = padLR)
        )
    }
}
