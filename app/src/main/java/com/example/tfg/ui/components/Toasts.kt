package com.example.tfg.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


class ToastHelper(private val context: android.content.Context) {
    fun exito(mensaje: String) = Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
    fun error(mensaje: String) = Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
}

@Composable
fun rememberToast(): ToastHelper {
    val ctx = LocalContext.current
    return ToastHelper(ctx)
}
