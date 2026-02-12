package com.ronaldveloz.recetas.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ronaldveloz.recetas.R
import com.ronaldveloz.recetas.domain.model.Recipe
import com.ronaldveloz.recetas.domain.model.UnitType
import com.ronaldveloz.recetas.viewmodel.RecipeListUiState

// Formateo simple para cantidades (UI)
private fun fmt(amount: Double, unit: UnitType): String =
    "${amount.toInt()} ${unit.name.lowercase()}"

@Composable
fun RecipeListScreen(
    ui: RecipeListUiState,
    onQueryChange: (String) -> Unit,
    onRecipeClick: (Recipe) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Título
        Text(
            text = stringResource(R.string.title_recipes),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))

        // Buscador
        OutlinedTextField(
            value = ui.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.search_hint)) },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        // Estado vacío
        if (ui.filteredRecipes.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.no_results))
            }
            return
        }

        // Lista
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(ui.filteredRecipes) { recipe ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRecipeClick(recipe) }
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            text = recipe.name, // nombre viene del JSON => NO va a strings.xml
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = stringResource(
                                R.string.yield_base,
                                fmt(recipe.baseYieldAmount, recipe.baseYieldUnit)
                            )
                        )
                    }
                }
            }
        }
    }
}

