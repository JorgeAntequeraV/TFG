package com.example.tfg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.tfg.ui.components.BackFab
import com.example.tfg.ui.components.GreenTextField
import com.example.tfg.ui.components.PrimaryButton
import com.example.tfg.ui.viewmodel.AuthViewModel


@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    val vm: AuthViewModel = viewModel()
    val state by vm.state.collectAsState()
    var usuario by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.reset() }

    val config = LocalConfiguration.current
    val padTop = config.screenHeightDp.dp / 16
    val padLR = config.screenWidthDp.dp / 32
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
                "Introduzca su nombre de usuario y le enviaremos un link al correo registrado para restablecer la contraseña.",
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(gap))
            GreenTextField(value = usuario, onValueChange = { usuario = it }, placeholder = "Usuario")
            Spacer(Modifier.height(gap))
            PrimaryButton(
                "Enviar",
                { vm.forgotPassword(usuario) },
                enabled = usuario.isNotBlank() && !state.loading
            )
            Spacer(Modifier.height(16.dp))
            if (state.loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            if (state.success) {
                Text(
                    "Si el usuario existe, te enviaremos un enlace al correo registrado.",
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
            state.error?.let { Text(it, color = Color.Red, textAlign = TextAlign.Center) }
        }
        BackFab(
            onClick = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(start = padLR)
        )
    }
}


@Composable
fun ResetPasswordScreen(token: String, onDone: () -> Unit) {
    val vm: AuthViewModel = viewModel()
    val state by vm.state.collectAsState()
    var nueva by remember { mutableStateOf("") }
    var nuevaRep by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { vm.reset() }
    LaunchedEffect(state.success) { if (state.success) onDone() }

    val config = LocalConfiguration.current
    val padTop = config.screenHeightDp.dp / 16
    val padLR = config.screenWidthDp.dp / 32

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
            Text(
                "Restablecer contraseña",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            if (token.isBlank()) {
                Text(
                    "Enlace no válido. Vuelve a solicitar el cambio desde la pantalla de inicio de sesión.",
                    color = Color.Red
                )
            } else {
                Text(
                    "Introduce tu nueva contraseña.",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(24.dp))
                GreenTextField(nueva, { nueva = it }, "Nueva contraseña", isPassword = true)
                Spacer(Modifier.height(12.dp))
                GreenTextField(nuevaRep, { nuevaRep = it }, "Repetir nueva contraseña", isPassword = true)
                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    PrimaryButton(
                        "Guardar",
                        {
                            localError = null
                            when {
                                nueva != nuevaRep -> localError = "Las contraseñas no coinciden"
                                nueva.contains(" ") -> localError = "La contraseña no puede tener espacios"
                                nueva.length < 4 -> localError = "Contraseña demasiado corta"
                                else -> vm.resetPassword(token, nueva)
                            }
                        },
                        enabled = nueva.isNotBlank() && nuevaRep.isNotBlank() && !state.loading
                    )
                }
                Spacer(Modifier.height(12.dp))
                if (state.loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                (localError ?: state.error)?.let { Text(it, color = Color.Red) }
            }
        }
        BackFab(
            onClick = onDone,
            modifier = Modifier.align(Alignment.BottomStart).padding(start = padLR)
        )
    }
}
