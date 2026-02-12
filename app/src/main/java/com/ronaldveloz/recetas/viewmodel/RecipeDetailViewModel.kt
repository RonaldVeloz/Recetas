package com.ronaldveloz.recetas.viewmodel

import androidx.lifecycle.ViewModel
import com.ronaldveloz.recetas.data.RecipeRepository
import com.ronaldveloz.recetas.domain.model.Recipe
import com.ronaldveloz.recetas.domain.usecase.ScaleRecipeUseCase
import com.ronaldveloz.recetas.domain.usecase.ScaleResult
import com.ronaldveloz.recetas.domain.usecase.ScaledRecipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

// Estado que usa la pantalla de detalle
// Contiene receta original + receta escalada + campos de UI
data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val scaled: ScaledRecipe? = null,
    val factorText: String = "",
    val changeMode: Boolean = false,
    val selectedIngredient: String = "",
    val ingredientAmountText: String = "",
    val error: String? = null
)

class RecipeDetailViewModel(
    private val repo: RecipeRepository,
    private val scaler: ScaleRecipeUseCase
) : ViewModel() {

    private val _ui = MutableStateFlow(RecipeDetailUiState())
    val ui: StateFlow<RecipeDetailUiState> = _ui

    /**
     * Cargamos la receta por ID cuando entramos en la pantalla
     */
    fun load(recipeId: String) {
        val recipe = repo.getById(recipeId)

        if (recipe == null) {
            _ui.update { it.copy(error = "Receta no encontrada") }
            return
        }

        val firstIngredient = recipe.ingredients.firstOrNull()?.name.orEmpty()

        _ui.value = RecipeDetailUiState(
            recipe = recipe,
            selectedIngredient = firstIngredient
        )

        // Al cargar, mostramos la receta en factor 1.0
        applyFactor(1.0)
    }

    /**
     * Escala la receta por un factor numérico (x2, x3, etc.)
     */
    fun applyFactor(factor: Double) {
        val recipe = _ui.value.recipe ?: return

        when (val result = scaler.byFactor(recipe, factor)) {
            is ScaleResult.Ok -> {
                _ui.update { it.copy(scaled = result.scaled, error = null) }
            }
            is ScaleResult.Error -> {
                _ui.update { it.copy(error = result.message) }
            }
        }
    }

    fun setFactorText(text: String) {
        _ui.update { it.copy(factorText = text) }
    }

    fun toggleChangeMode(enabled: Boolean) {
        _ui.update { it.copy(changeMode = enabled, ingredientAmountText = "") }
    }

    fun setSelectedIngredient(name: String) {
        _ui.update { it.copy(selectedIngredient = name) }
    }

    fun setIngredientAmountText(text: String) {
        _ui.update { it.copy(ingredientAmountText = text) }
    }

    /**
     * Recalcula la receta usando la cantidad de un ingrediente como referencia
     */
    fun recalcByIngredient() {
        val recipe = _ui.value.recipe ?: return

        val amount = _ui.value.ingredientAmountText
            .replace(',', '.')
            .toDoubleOrNull()

        if (amount == null) {
            _ui.update { it.copy(error = "Cantidad inválida") }
            return
        }

        when (val result = scaler.byIngredient(recipe, _ui.value.selectedIngredient, amount)) {
            is ScaleResult.Ok -> {
                _ui.update { it.copy(scaled = result.scaled, error = null) }
            }
            is ScaleResult.Error -> {
                _ui.update { it.copy(error = result.message) }
            }
        }
    }
}
