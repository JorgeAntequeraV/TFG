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
import com.example.tfg.ui.components.BackBoton
import com.example.tfg.ui.components.GreenTextField
import com.example.tfg.ui.components.PrimaryButton
import com.example.tfg.ui.components.RecaptchaWebView
import com.example.tfg.ui.components.rememberRecaptchaController
import com.example.tfg.ui.components.rememberToast
import com.example.tfg.ui.theme.GreenAccent
import com.example.tfg.ui.viewmodel.AuthViewModel

private enum class CaptchaState { IDLE, VERIFICANDO, OK, ERROR }

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
    var recaptchaToken by remember { mutableStateOf<String?>(null) }
    var captchaState by remember { mutableStateOf(CaptchaState.IDLE) }
    var localError by remember { mutableStateOf<String?>(null) }

    val recaptchaController = rememberRecaptchaController()

    val toast = rememberToast()
    LaunchedEffect(state.success) {
        if (state.success) {
            toast.exito("Cuenta creada con éxito")
            onRegistroSuccess()
            vm.reset()
        }
    }
    LaunchedEffect(state.error) { state.error?.let { toast.error(it) } }

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
        if (recaptchaToken.isNullOrBlank()) {
            localError = "Marca 'No soy un robot'"; return
        }
        vm.registro(nombre.trim(), email.trim(), usuario.trim(), contrasena, recaptchaToken)
    }

//Necesita un click del usuariuo para activarse
    RecaptchaWebView(
        controller = recaptchaController,
        onTokenReceived = { token ->
            recaptchaToken = token
            captchaState = CaptchaState.OK
        },
        onError = {
            recaptchaToken = null
            captchaState = CaptchaState.ERROR
        }
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
        //Pulsar oara activar el captcha, si no se me caducaban
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(6.dp))
                .clickable(enabled = captchaState == CaptchaState.IDLE || captchaState == CaptchaState.ERROR) {
                    captchaState = CaptchaState.VERIFICANDO
                    recaptchaController.execute()
                }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(28.dp)
                    .background(
                        if (captchaState == CaptchaState.OK) GreenAccent else Color.Transparent,
                        RoundedCornerShape(4.dp)
                    )
                    .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                when (captchaState) {
                    CaptchaState.OK -> Icon(Icons.Default.Check, null, tint = Color.Black)
                    CaptchaState.VERIFICANDO -> CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    else -> Unit
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                when (captchaState) {
                    CaptchaState.IDLE -> "No soy un robot"
                    CaptchaState.VERIFICANDO -> "Verificando…"
                    CaptchaState.OK -> "No soy un robot"
                    CaptchaState.ERROR -> "Error — toca para reintentar"
                },
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(28.dp))
        PrimaryButton(
            text = "Crear cuenta",
            onClick = { validarYRegistrar() },
            modifier = Modifier.fillMaxWidth(0.6f),
            enabled = nombre.isNotBlank() && usuario.isNotBlank() && email.isNotBlank() &&
                contrasena.isNotBlank() && captchaState == CaptchaState.OK && !state.loading
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
        BackBoton(onClick = onBack, modifier = Modifier.align(Alignment.BottomStart).padding(start = padLeftRight))
    }
}
