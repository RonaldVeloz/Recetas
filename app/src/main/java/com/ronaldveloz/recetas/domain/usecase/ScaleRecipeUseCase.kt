package com.ronaldveloz.recetas.domain.usecase

import com.ronaldveloz.recetas.domain.model.Ingredient
import com.ronaldveloz.recetas.domain.model.Recipe

/**
 * Resultado del escalado:
 * - factor: factor aplicado (1.0, 2.0, etc.)
 * - yieldAmount: rendimiento escalado
 * - ingredients: ingredientes escalados
 */
data class ScaledRecipe(
    val factor: Double,
    val yieldAmount: Double,
    val ingredients: List<Ingredient>
)

/**
 * Tipos de error del dominio.
 * Importante: aquí NO ponemos strings, porque el dominio no debería depender de UI / idioma.
 */
enum class ScaleError {
    INVALID_FACTOR,
    INVALID_AMOUNT,
    INGREDIENT_NOT_FOUND,
    INVALID_BASE_AMOUNT
}

/**
 * Resultado del caso de uso:
 * - Ok: devuelve ScaledRecipe
 * - Error: devuelve un ScaleError (no un mensaje)
 */
sealed interface ScaleResult {
    data class Ok(val scaled: ScaledRecipe) : ScaleResult
    data class Error(val error: ScaleError) : ScaleResult
}

/**
 * Caso de uso: escalar receta manteniendo proporciones.
 * Aquí vive la “lógica de negocio”.
 */
class ScaleRecipeUseCase {

    fun byFactor(recipe: Recipe, factor: Double): ScaleResult {
        // Validación básica: factor debe ser positivo
        if (factor <= 0.0) return ScaleResult.Error(ScaleError.INVALID_FACTOR)

        return ScaleResult.Ok(
            ScaledRecipe(
                factor = factor,
                yieldAmount = recipe.baseYieldAmount * factor,
                ingredients = recipe.ingredients.map { it.copy(amount = it.amount * factor) }
            )
        )
    }

    fun byIngredient(recipe: Recipe, ingredientName: String, newAmount: Double): ScaleResult {
        // Validación básica
        if (newAmount <= 0.0) return ScaleResult.Error(ScaleError.INVALID_AMOUNT)

        val ref = recipe.ingredients.firstOrNull {
            it.name.equals(ingredientName, ignoreCase = true)
        } ?: return ScaleResult.Error(ScaleError.INGREDIENT_NOT_FOUND)

        if (ref.amount <= 0.0) return ScaleResult.Error(ScaleError.INVALID_BASE_AMOUNT)

        val factor = newAmount / ref.amount
        return byFactor(recipe, factor)
    }
}
