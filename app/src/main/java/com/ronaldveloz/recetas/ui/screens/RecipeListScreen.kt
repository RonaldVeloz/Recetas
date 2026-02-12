package com.ronaldveloz.recetas.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ronaldveloz.recetas.domain.model.Recipe
import com.ronaldveloz.recetas.domain.model.UnitType
import com.ronaldveloz.recetas.viewmodel.RecipeListUiState

/**
 * Función auxiliar para formatear cantidades.
 * La dejo privada porque solo se usa en esta pantalla.
 *
 * De momento redondeo a Int para que visualmente sea limpio.
 * Más adelante puedo mejorar esto para mostrar decimales si hace falta.
 */
private fun fmt(amount: Double, unit: UnitType): String =
    "${amount.toInt()} ${unit.name.lowercase()}"

/**
 * Pantalla principal de listado de recetas.
 *
 * Esta pantalla NO sabe nada del repositorio ni de la lógica.
 * Solo recibe:
 *  - ui: el estado que viene del ViewModel
 *  - onQueryChange: callback para el buscador
 *  - onRecipeClick: callback cuando el usuario pulsa una receta
 *
 * De esta forma mantenemos la UI desacoplada (Clean Architecture).
 */
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

        // Título principal de la pantalla
        Text(
            text = "Recetas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))

        /**
         * Campo de búsqueda.
         *
         * - value viene del estado (ui.query).
         * - cada vez que el usuario escribe algo, llamamos al callback.
         * - El ViewModel se encarga de recalcular el filtro.
         */
        OutlinedTextField(
            value = ui.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Buscar (receta o ingrediente)") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        /**
         * Si no hay recetas filtradas mostramos mensaje.
         * Esto mejora la UX cuando la búsqueda no devuelve resultados.
         */
        if (ui.filteredRecipes.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron recetas",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        } else {

            /**
             * Lista de recetas.
             *
             * Uso LazyColumn porque:
             * - Es eficiente con listas grandes.
             * - Solo renderiza lo que está visible.
             */
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(ui.filteredRecipes) { recipe ->

                    /**
                     * Cada receta es una Card clickable.
                     * Cuando se pulsa, avisamos al ViewModel/Navegación.
                     */
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRecipeClick(recipe) }
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {

                            // Nombre de la receta
                            Text(
                                text = recipe.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(Modifier.height(4.dp))

                            // Rendimiento base (sin escalado)
                            Text(
                                text = "Rinde (base): ${
                                    fmt(
                                        recipe.baseYieldAmount,
                                        recipe.baseYieldUnit
                                    )
                                }"
                            )
                        }
                    }
                }
            }
        }
    }
}

