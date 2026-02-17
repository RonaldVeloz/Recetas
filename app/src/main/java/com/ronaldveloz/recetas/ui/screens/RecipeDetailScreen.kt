package com.ronaldveloz.recetas.ui.screens

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
import com.ronaldveloz.recetas.domain.model.UnitType
import com.ronaldveloz.recetas.ui.components.IngredientDropdown
import com.ronaldveloz.recetas.viewmodel.RecipeDetailUiState

/**
 * Función auxiliar para mostrar cantidades en la UI.
 * No va a strings.xml porque el valor final es dinámico.
 */
private fun fmt(amount: Double, unit: UnitType): String =
    "${amount.toInt()} ${unit.name.lowercase()}"

/**
 * Pantalla de detalle de receta.
 *
 * Buenas prácticas:
 * - La UI NO calcula lógica de negocio: solo pinta el estado (ui) y dispara eventos (callbacks).
 * - Los textos fijos se sacan a strings.xml (stringResource).
 */
@Composable
fun RecipeDetailScreen(
    ui: RecipeDetailUiState,
    onBack: () -> Unit,
    onApplyFactor: (Double) -> Unit,
    onSetFactorText: (String) -> Unit,
    onToggleChangeMode: (Boolean) -> Unit,
    onSelectIngredient: (String) -> Unit,
    onSetIngredientAmountText: (String) -> Unit,
    onRecalcByIngredient: () -> Unit
) {
    val recipe = ui.recipe
    val scaled = ui.scaled

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // --- Cabecera: volver + título ---
        Row(verticalAlignment = Alignment.CenterVertically) {

            TextButton(onClick = onBack) {
                Text(stringResource(R.string.back_arrow))
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = recipe?.name.orEmpty(), // el nombre viene del JSON (contenido), no de strings.xml
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(8.dp))

        // --- Si el ViewModel dejó un error, lo mostramos aquí ---
        ui.errorResId?.let { resID ->
            Text(
                text = stringResource(resID),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
        }

        // --- Rendimiento actual (escalado) ---
        if (scaled != null && recipe != null) {
            Text(
                text = stringResource(
                    R.string.yield,
                    fmt(scaled.yieldAmount, recipe.baseYieldUnit)
                ),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(Modifier.height(12.dp))

        // --- Botones rápidos x2, x3, x4 + reset ---
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

            // Uso stringResource con formato para no hardcodear "x2", "x3", etc.
            Button(onClick = { onApplyFactor(2.0) }) {
                Text(stringResource(R.string.multiply_by, 2))
            }
            Button(onClick = { onApplyFactor(3.0) }) {
                Text(stringResource(R.string.multiply_by, 3))
            }
            Button(onClick = { onApplyFactor(4.0) }) {
                Text(stringResource(R.string.multiply_by, 4))
            }

            OutlinedButton(onClick = { onApplyFactor(1.0) }) {
                Text(stringResource(R.string.reset))
            }
        }

        Spacer(Modifier.height(12.dp))

        // --- Factor libre (el usuario mete un número y aplica el escalado) ---
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {

                Text(
                    text = stringResource(R.string.free_factor),
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = ui.factorText,
                    onValueChange = { onSetFactorText(it.replace(',', '.')) }, // acepto coma o punto
                    label = { Text(stringResource(R.string.factor_example)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(6.dp))

                Button(
                    onClick = {
                        // Parseo simple: si no es número, no hago nada (y el VM puede sacar error si quieres)
                        ui.factorText.toDoubleOrNull()?.let { onApplyFactor(it) }
                    }
                ) {
                    Text(stringResource(R.string.apply))
                }

                // Si tenemos factor actual disponible, lo mostramos como referencia
                if (scaled != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.current_factor, "%.3f".format(scaled.factor)),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // --- Ajustar por ingrediente (esto fue lo que “se perdió” en la versión simplificada) ---
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.adjust_by_ingredient),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )

                    // Switch para activar/desactivar el modo
                    Switch(
                        checked = ui.changeMode,
                        onCheckedChange = onToggleChangeMode
                    )
                }

                // Solo mostramos los controles si el modo está activo y hay receta cargada
                if (ui.changeMode && recipe != null) {
                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = stringResource(R.string.ingredient_reference),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(6.dp))

                    // Dropdown con la lista de ingredientes de la receta
                    IngredientDropdown(
                        options = recipe.ingredients.map { it.name },
                        selected = ui.selectedIngredient.ifBlank { recipe.ingredients.first().name },
                        onSelected = onSelectIngredient,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    // Determinamos la unidad del ingrediente seleccionado para mostrarla en el label
                    val selectedName = ui.selectedIngredient.ifBlank { recipe.ingredients.first().name }
                    val selectedUnit = recipe.ingredients.first { it.name == selectedName }.unit

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = ui.ingredientAmountText,
                            onValueChange = { onSetIngredientAmountText(it.replace(',', '.')) },
                            label = {
                                Text(
                                    stringResource(
                                        R.string.new_amount,
                                        selectedUnit.name.lowercase()
                                    )
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        Button(onClick = onRecalcByIngredient) {
                            Text(stringResource(R.string.recalculate))
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.adjust_keeps_proportions),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // --- Ingredientes (ya escalados) ---
        Text(
            text = stringResource(R.string.ingredients),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            if (scaled != null) {
                items(scaled.ingredients) { ing ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(ing.name, modifier = Modifier.weight(1f))
                            Text(fmt(ing.amount, ing.unit), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(12.dp))

                // --- Elaboración ---
                Text(
                    text = stringResource(R.string.method),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(6.dp))

                Card(Modifier.fillMaxWidth()) {
                    Text(
                        text = recipe?.method.orEmpty(), // viene del JSON
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}