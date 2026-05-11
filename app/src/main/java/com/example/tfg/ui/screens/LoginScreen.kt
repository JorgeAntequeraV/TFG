package com.example.tfg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tfg.ui.components.GoogleSignInButton
import com.example.tfg.ui.components.GreenTextField
import com.example.tfg.ui.components.PrimaryButton
import com.example.tfg.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegistro: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val vm: AuthViewModel = viewModel()
    val state by vm.state.collectAsState()

    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    LaunchedEffect(state.success) {
        if (state.success) {
            onLoginSuccess()
            vm.reset()
        }
    }

    val config = LocalConfiguration.current
    val padTopBottom = config.screenHeightDp.dp / 16
    val padLeftRight = config.screenWidthDp.dp / 32

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = padLeftRight, vertical = padTopBottom),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo()
        Spacer(Modifier.height(24.dp))
        Text(
            "Bienvenido a BuyNotes",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))

        GreenTextField(
            value = usuario,
            onValueChange = { usuario = it },
            placeholder = "Usuario"
        )
        Spacer(Modifier.height(12.dp))
        GreenTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            placeholder = "Contraseña",
            isPassword = true
        )
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth()) {
            Text(
                "He olvidado mi contraseña",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { onForgotPassword() },
                color = MaterialTheme.colorScheme.onBackground,
                textDecoration = TextDecoration.Underline,
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(28.dp))
        // Continuar con Google (3/4 del largo) — Google Sign-In real
        GoogleSignInButton(
            onIdTokenReceived = { idToken -> vm.loginConGoogle(idToken) },
            onError = { msg -> vm.mostrarError(msg) },
            modifier = Modifier.fillMaxWidth(0.75f),
            enabled = !state.loading
        )
        Spacer(Modifier.height(28.dp))

        // Iniciar sesión (2/4 del largo)
        PrimaryButton(
            text = "Iniciar Sesión",
            onClick = { vm.login(usuario.trim(), contrasena) },
            modifier = Modifier.fillMaxWidth(0.5f),
            enabled = usuario.isNotBlank() && contrasena.isNotBlank() && !state.loading
        )

        Spacer(Modifier.height(24.dp))
        Text(
            "No tengo cuenta, registrarme",
            modifier = Modifier.clickable { onGoToRegistro() },
            color = MaterialTheme.colorScheme.onBackground,
            textDecoration = TextDecoration.Underline,
            fontSize = 14.sp
        )

        if (state.loading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color.Red, fontSize = 14.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun Logo() {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.res.painterResource(id = com.example.tfg.R.drawable.logo_buynotes),
        contentDescription = "Logo BuyNotes",
        modifier = Modifier.size(140.dp)
    )
}
