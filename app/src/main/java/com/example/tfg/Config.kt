package com.example.tfg

/**
 * Configuración global de la app — editar y recompilar para cambiar.
 * Mantener todo lo configurable aquí para no tener que buscarlo por el proyecto.
 */
object Config {

    // ==========================================================
    //  URL BASE DE LA API
    // ==========================================================
    //  - Emulador Android (backend en localhost de tu PC):
    //      "http://10.0.2.2:8080/"
    //  - Dispositivo físico en la misma red WiFi (sustituye por la IP de tu PC):
    //      "http://192.168.1.42:8080/"
    //  - Backend desplegado:
    //      "https://tu-dominio.com/"
    //
    //  Debe terminar SIEMPRE con "/".
    // ==========================================================
    const val BASE_URL: String = "http://10.0.2.2:8080/"
}
