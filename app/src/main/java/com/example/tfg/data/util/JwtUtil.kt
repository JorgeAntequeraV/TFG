package com.example.tfg.data.util

import android.util.Base64
import org.json.JSONObject

data class JwtClaims(
    val sub: String?,
    val userId: Long?,
    val role: String?
)

object JwtUtil {
    fun parse(token: String): JwtClaims? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP))
            val json = JSONObject(payload)
            JwtClaims(
                sub = if (json.has("sub")) json.optString("sub") else null,
                userId = if (json.has("userId")) json.optLong("userId") else null,
                role = if (json.has("role")) json.optString("role") else null
            )
        } catch (e: Exception) {
            null
        }
    }
}
