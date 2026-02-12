package com.ronaldveloz.recetas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ronaldveloz.recetas.domain.model.UnitType
import com.ronaldveloz.recetas.ui.components.IngredientDropdown
import com.ronaldveloz.recetas.viewmodel.RecipeDetailUiState
import kotlin.math.roundToInt

private fun fmt(amount: Double, unit: UnitType): String =
    when (unit) {
        UnitType.UDS -> "${amount.roundToInt()} ${unit.name.lowercase()}"
        else -> "${amount.roundToInt()} ${unit.name.lowercase()}"
    }

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
    val focus = LocalFocusManager.current
    val recipe = ui.recipe
    val scaled = ui.scaled

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Volver") }
            Spacer(Modifier.width(8.dp))
            Text(recipe?.name ?: "Receta", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        }

        if (ui.error != null) {
            Spacer(Modifier.height(6.dp))
            Text(ui.error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(8.dp))
        if (scaled != null && recipe != null) {
            Text("Rinde: ${fmt(scaled.yieldAmount, recipe.baseYieldUnit)}", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { onApplyFactor(2.0); onToggleChangeMode(false); focus.clearFocus() }) { Text("x2") }
            Button(onClick = { onApplyFactor(3.0); onToggleChangeMode(false); focus.clearFocus() }) { Text("x3") }
            Button(onClick = { onApplyFactor(4.0); onToggleChangeMode(false); focus.clearFocus() }) { Text("x4") }
            OutlinedButton(onClick = { onApplyFactor(1.0); onToggleChangeMode(false); onSetFactorText(""); onSetIngredientAmountText(""); focus.clearFocus() }) {
                Text("Reset")
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text("Factor libre", fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = ui.factorText,
                        onValueChange = { onSetFactorText(it.replace(',', '.')) },
                        label = { Text("Ej: 1.5") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = {
                        val f = ui.factorText.toDoubleOrNull()
                        if (f != null && f > 0) {
                            onApplyFactor(f)
                            onToggleChangeMode(false)
                            onSetIngredientAmountText("")
                            focus.clearFocus()
                        }
                    }) { Text("Aplicar") }
                }

                Spacer(Modifier.height(6.dp))
                if (scaled != null) Text("Factor actual: ${"%.3f".format(scaled.factor)}", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Ajustar por ingrediente", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Switch(checked = ui.changeMode, onCheckedChange = {
                        onToggleChangeMode(it)
                        focus.clearFocus()
                    })
                }

                if (ui.changeMode && recipe != null) {
                    Spacer(Modifier.height(10.dp))
                    IngredientDropdown(
                        options = recipe.ingredients.map { it.name },
                        selected = ui.selectedIngredient.ifBlank { recipe.ingredients.first().name },
                        onSelected = onSelectIngredient,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    val unit = recipe.ingredients.first { it.name == ui.selectedIngredient }.unit

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = ui.ingredientAmountText,
                            onValueChange = { onSetIngredientAmountText(it.replace(',', '.')) },
                            label = { Text("Nueva cantidad (${unit.name.lowercase()})") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = { onRecalcByIngredient(); focus.clearFocus() }) { Text("Recalcular") }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("Ingredientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {

            if (scaled != null) {
                items(scaled.ingredients) { ing ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(ing.name, modifier = Modifier.weight(1f))
                            Text(fmt(ing.amount, ing.unit), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(10.dp))
                Text("Elaboración", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                Card(Modifier.fillMaxWidth()) {
                    Text(recipe?.method.orEmpty(), modifier = Modifier.padding(14.dp))
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
