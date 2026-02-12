package com.ronaldveloz.recetas.domain.usecase

import com.ronaldveloz.recetas.domain.model.Ingredient
import com.ronaldveloz.recetas.domain.model.Recipe


data class ScaledRecipe(
    val factor: Double,
    val yieldAmount: Double,
    val ingredients: List<Ingredient>
)

sealed interface ScaleResult{
    data class Ok(val scaled:ScaledRecipe) : ScaleResult
    data class Error(val message:String) : ScaleResult
}

class ScaleRecipeUseCase{
    fun byFactor(recipe: Recipe, factor: Double): ScaleResult{
        if(factor <= 0.0) return ScaleResult.Error("Factor deber ser > 0.0")
        return ScaleResult.Ok(
            ScaledRecipe(
                factor = factor,
                yieldAmount = recipe.baseYieldAmount * factor,
                ingredients = recipe.ingredients.map { it.copy(amount = it.amount * factor) }
            )
        )

}
    fun byIngredient(recipe: Recipe, ingredientName: String, newAmount: Double): ScaleResult {
        if (newAmount <= 0.0) return ScaleResult.Error("La cantidad debe ser > 0.")
        val ref = recipe.ingredients.firstOrNull { it.name.equals(ingredientName, ignoreCase = true) }
            ?: return ScaleResult.Error("Ingrediente no encontrado.")
        if (ref.amount <= 0.0) return ScaleResult.Error("Cantidad base inválida.")
        val factor = newAmount / ref.amount
        return byFactor(recipe, factor)
    }
}
