package com.ronaldveloz.recetas.viewmodel

import androidx.lifecycle.ViewModel
import com.ronaldveloz.recetas.data.RecipeRepository
import com.ronaldveloz.recetas.domain.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class RecipeListUiState(
    val allRecipes: List<Recipe> = emptyList(),     // Lista completa (la “fuente de verdad”)
    val query: String = "",                         // Texto del buscador
    val filteredRecipes: List<Recipe> = emptyList() // Lo que se muestra (filtrado por query)
)

class RecipeListViewModel(
    private val repo: RecipeRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(RecipeListUiState())
    val ui: StateFlow<RecipeListUiState> = _ui

    fun load() {
        // Cargamos todas las recetas del repositorio una única vez
        val recipes = repo.getAll().sortedBy { it.name.lowercase() }

        // Inicializamos el estado: al principio, el filtro muestra todo
        _ui.value = RecipeListUiState(
            allRecipes = recipes,
            query = "",
            filteredRecipes = recipes
        )
    }

    fun setQuery(newQuery: String) {
        // Guardamos el texto del usuario y recalculamos el filtro
        _ui.update { current ->
            val q = newQuery.trim().lowercase()

            val filtered = if (q.isBlank()) {
                current.allRecipes
            } else {
                current.allRecipes.filter { r ->
                    r.name.lowercase().contains(q) ||
                            r.ingredients.any { it.name.lowercase().contains(q) }
                }
            }

            current.copy(
                query = newQuery,
                filteredRecipes = filtered
            )
        }
    }
}
