package com.example.tfg.data.session

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("buynotes_session")

class SessionManager(private val context: Context) {

    private val tokenKey = stringPreferencesKey("jwt_token")
    private val userIdKey = longPreferencesKey("user_id")
    private val nombreUsuarioKey = stringPreferencesKey("nombre_usuario")
    private val rolKey = stringPreferencesKey("rol")
    private val temaOscuroKey = booleanPreferencesKey("tema_oscuro")

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[tokenKey] }
    val userIdFlow: Flow<Long?> = context.dataStore.data.map { it[userIdKey] }
    val nombreUsuarioFlow: Flow<String?> = context.dataStore.data.map { it[nombreUsuarioKey] }
    val rolFlow: Flow<String?> = context.dataStore.data.map { it[rolKey] }
    val temaOscuroFlow: Flow<Boolean> = context.dataStore.data.map { it[temaOscuroKey] ?: false }

    suspend fun saveSession(token: String, userId: Long?, nombreUsuario: String?, rol: String?) {
        context.dataStore.edit {
            it[tokenKey] = token
            if (userId != null) it[userIdKey] = userId
            if (nombreUsuario != null) it[nombreUsuarioKey] = nombreUsuario
            if (rol != null) it[rolKey] = rol
        }
    }

    suspend fun saveTemaOscuro(value: Boolean) {
        context.dataStore.edit { it[temaOscuroKey] = value }
    }

    suspend fun saveNombreUsuario(nombre: String) {
        context.dataStore.edit { it[nombreUsuarioKey] = nombre }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun getTokenSync(): String? = tokenFlow.firstOrNull()
}
