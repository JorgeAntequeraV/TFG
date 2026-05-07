package com.example.tfg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tfg.ui.components.GreenTextField
import com.example.tfg.ui.components.PrimaryButton
import com.example.tfg.ui.theme.GreenAccent
import com.example.tfg.ui.viewmodel.AuthViewModel

@Composable
fun RegistroScreen(
    onRegistroSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val vm: AuthViewModel = viewModel()
    val state by vm.state.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var noSoyRobot by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.success) {
        if (state.success) {
            onRegistroSuccess()
            vm.reset()
        }
    }

    val config = LocalConfiguration.current
    val padTopBottom = config.screenHeightDp.dp / 16
    val padLeftRight = config.screenWidthDp.dp / 32

    fun validarYRegistrar() {
        localError = null
        if (!email.contains("@") || !email.substringAfter("@").contains(".")) {
            localError = "Correo no válido"; return
        }
        if (contrasena.contains(" ")) {
            localError = "La contraseña no puede tener espacios"; return
        }
        if (usuario.contains(" ") || !usuario.matches(Regex("^[A-Za-z0-9_]+$"))) {
            localError = "Usuario no válido"; return
        }
        vm.registro(nombre.trim(), email.trim(), usuario.trim(), contrasena)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = padLeftRight, vertical = padTopBottom),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo()
        Spacer(Modifier.height(20.dp))
        Text(
            "Bienvenido a BuyNotes",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))

        GreenTextField(value = nombre, onValueChange = { nombre = it }, placeholder = "Nombre")
        Spacer(Modifier.height(10.dp))
        GreenTextField(value = usuario, onValueChange = { usuario = it }, placeholder = "Usuario")
        Spacer(Modifier.height(10.dp))
        GreenTextField(value = email, onValueChange = { email = it }, placeholder = "Correo")
        Spacer(Modifier.height(10.dp))
        GreenTextField(value = contrasena, onValueChange = { contrasena = it }, placeholder = "Contraseña", isPassword = true)

        Spacer(Modifier.height(24.dp))
        // reCAPTCHA visual — pendiente de integración (no bloquea el registro)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(28.dp)
                    .background(if (noSoyRobot) GreenAccent else Color.Transparent, RoundedCornerShape(4.dp))
                    .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp))
                    .clickable { noSoyRobot = !noSoyRobot },
                contentAlignment = Alignment.Center
            ) {
                if (noSoyRobot) Icon(Icons.Default.Check, null, tint = Color.Black)
            }
            Spacer(Modifier.width(12.dp))
            Text("No soy un robot", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
        }

        Spacer(Modifier.height(28.dp))
        PrimaryButton(
            text = "Crear cuenta",
            onClick = { validarYRegistrar() },
            modifier = Modifier.fillMaxWidth(0.6f),
            enabled = nombre.isNotBlank() && usuario.isNotBlank() && email.isNotBlank() && contrasena.isNotBlank() && !state.loading
        )

        Spacer(Modifier.height(12.dp))
        Text(
            "Volver",
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.clickable { onBack() },
            fontSize = 14.sp
        )

        if (state.loading) {
            Spacer(Modifier.height(12.dp))
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        (localError ?: state.error)?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color.Red, textAlign = TextAlign.Center)
        }
    }
}
