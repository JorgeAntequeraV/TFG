package com.example.tfg.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.tfg.data.model.ProductoCatalogoDTO
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.first

private val Context.presetsDataStore by preferencesDataStore("buynotes_presets")


class PresetsCache(private val context: Context) {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val productosAdapter = moshi.adapter<List<ProductoCatalogoDTO>>(
        Types.newParameterizedType(List::class.java, ProductoCatalogoDTO::class.java)
    )
    private val stringsAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(List::class.java, String::class.java)
    )

    private val productosKey = stringPreferencesKey("productos_defecto")
    private val supermercadosKey = stringPreferencesKey("supermercados_defecto")

    suspend fun productosCacheados(): List<ProductoCatalogoDTO>? {
        val json = context.presetsDataStore.data.first()[productosKey] ?: return null
        return runCatching { productosAdapter.fromJson(json) }.getOrNull()
    }

    suspend fun guardarProductos(productos: List<ProductoCatalogoDTO>) {
        val json = productosAdapter.toJson(productos)
        context.presetsDataStore.edit { it[productosKey] = json }
    }

    suspend fun supermercadosCacheados(): List<String>? {
        val json = context.presetsDataStore.data.first()[supermercadosKey] ?: return null
        return runCatching { stringsAdapter.fromJson(json) }.getOrNull()
    }

    suspend fun guardarSupermercados(supermercados: List<String>) {
        val json = stringsAdapter.toJson(supermercados)
        context.presetsDataStore.edit { it[supermercadosKey] = json }
    }

    suspend fun limpiar() {
        context.presetsDataStore.edit { it.clear() }
    }
}
