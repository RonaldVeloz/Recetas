package com.ronaldveloz.recetas.data

import android.content.Context
import com.ronaldveloz.recetas.data.dto.RecipeDto
import com.ronaldveloz.recetas.data.mapper.toDomain
import com.ronaldveloz.recetas.domain.model.Recipe
import kotlinx.serialization.json.Json

class JsonAssetRecipeRepository(
    // Guardamos el context para poder leer el fichero desde assets
    // Importante: desde AppNav le pasamos applicationContext para evitar fugas de memoria
    private val context: Context
) : RecipeRepository {

    /**
     * Configuración del parser JSON.
     * - ignoreUnknownKeys: si mañana añado campos nuevos al JSON, no rompe.
     * - isLenient: acepta JSON “un poco flexible” (por ejemplo espacios o formatos más permisivos).
     * - encodeDefaults: si en un futuro serializamos, incluiría valores por defecto (no hace daño).
     */
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    /**
     * Cache simple en memoria:
     * - La primera vez se lee el JSON.
     * - A partir de ahí devolvemos lo ya cargado.
     *
     * Esto hace que la app sea rápida y no esté leyendo assets todo el tiempo.
     */
    private var cache: List<Recipe>? = null

    override fun getAll(): List<Recipe> {
        // Si ya está en cache, devolvemos directamente
        cache?.let { return it }

        return try {
            // 1) Leemos el fichero JSON de assets como texto
            val text = context.assets.open("recipes.json")
                .bufferedReader()
                .use { it.readText() }

            // 2) Lo parseamos a DTOs (clases “de datos” pensadas para JSON)
            val dtoList = json.decodeFromString<List<RecipeDto>>(text)

            // 3) Convertimos DTO -> Domain (para que el dominio no dependa del JSON)
            val domainList = dtoList
                .map { it.toDomain() }
                .sortedBy { it.name.lowercase() }

            // 4) Guardamos en cache y devolvemos
            cache = domainList
            domainList

        } catch (e: Exception) {
            // Si hay un error (JSON mal, archivo no encontrado, etc.),
            // prefiero no tumbar la app y devolver lista vacía.
            // Luego esto lo mejoraremos con logs / pantalla de error.
            cache = emptyList()
            emptyList()
        }
    }

    override fun getById(id: String): Recipe? {
        // Reutilizamos getAll() para aprovechar el cache
        return getAll().firstOrNull { it.id == id }
    }
}
