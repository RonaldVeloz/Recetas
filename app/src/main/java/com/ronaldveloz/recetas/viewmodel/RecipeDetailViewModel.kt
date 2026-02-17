package com.ronaldveloz.recetas.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.ronaldveloz.recetas.data.RecipeRepository
import com.ronaldveloz.recetas.domain.model.Recipe
import com.ronaldveloz.recetas.domain.usecase.ScaleError
import com.ronaldveloz.recetas.domain.usecase.ScaleRecipeUseCase
import com.ronaldveloz.recetas.domain.usecase.ScaleResult
import com.ronaldveloz.recetas.domain.usecase.ScaledRecipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.ronaldveloz.recetas.R



private fun ScaleError.toStringRes(): Int = when(this){
    ScaleError.INVALID_FACTOR->R.string.error_invalid_factor
    ScaleError.INVALID_AMOUNT->R.string.error_invalid_amount
    ScaleError.INGREDIENT_NOT_FOUND->R.string.error_ingredient_not_found
    ScaleError.INVALID_BASE_AMOUNT -> R.string.error_invalid_base_amount
}

// Estado que usa la pantalla de detalle
// Contiene receta original + receta escalada + campos de UI
data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val scaled: ScaledRecipe? = null,
    val factorText: String = "",
    val changeMode: Boolean = false,
    val selectedIngredient: String = "",
    val ingredientAmountText: String = "",
    @StringRes val errorResId: Int? = null
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
            _ui.update { it.copy(errorResId = R.string.error_recipe_not_found) }
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
                _ui.update { it.copy(scaled = result.scaled, errorResId = null) }
            }
            is ScaleResult.Error -> {
                _ui.update {  it.copy(errorResId = result.error.toStringRes()) }
            }
        }
    }
    fun setFactorText(text: String) = _ui.update { it.copy(factorText = text, errorResId = null) }
    fun toggleChangeMode(enabled: Boolean) = _ui.update { it.copy(changeMode = enabled, ingredientAmountText = "", errorResId = null) }
    fun setSelectedIngredient(name: String) = _ui.update { it.copy(selectedIngredient = name, errorResId = null) }
    fun setIngredientAmountText(text: String) = _ui.update { it.copy(ingredientAmountText = text, errorResId = null) }

    /**
     * Recalcula la receta usando la cantidad de un ingrediente como referencia
     */
    fun recalcByIngredient() {
        val recipe = _ui.value.recipe ?: return

        val newAmt = _ui.value.ingredientAmountText
            .replace(',', '.')
            .toDoubleOrNull()

        if (newAmt == null) {
            _ui.update { it.copy(errorResId = R.string.error_invalid_amount) }
            return
        }

        when (val result = scaler.byIngredient(recipe, _ui.value.selectedIngredient, newAmt)) {
            is ScaleResult.Ok -> _ui.update { it.copy(scaled = result.scaled, errorResId = null) }
            is ScaleResult.Error -> _ui.update { it.copy(errorResId = result.error.toStringRes()) }
        }

    }
}
