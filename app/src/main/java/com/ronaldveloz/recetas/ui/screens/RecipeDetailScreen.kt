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
import com.ronaldveloz.recetas.viewmodel.RecipeDetailUiState

/**
 * Función auxiliar para mostrar cantidades en la UI.
 * Aquí no usamos strings.xml porque es formateo dinámico.
 */
private fun fmt(amount: Double, unit: UnitType): String =
    "${amount.toInt()} ${unit.name.lowercase()}"

/**
 * Pantalla de detalle de receta.
 *
 * No contiene lógica de negocio.
 * Solo muestra estado y lanza callbacks al ViewModel.
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

        /**
         * Botón volver + título
         */
        Row(verticalAlignment = Alignment.CenterVertically) {

            TextButton(onClick = onBack) {
                Text(stringResource(R.string.back_arrow))
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = recipe?.name ?: "",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(8.dp))

        /**
         * Rendimiento actual (escalado)
         */
        scaled?.let {
            Text(
                text = stringResource(
                    R.string.yield,
                    fmt(it.yieldAmount, recipe!!.baseYieldUnit)
                ),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(Modifier.height(12.dp))

        /**
         * Botones rápidos x2 x3 x4
         */
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

            Button(onClick = { onApplyFactor(2.0) }) { Text("x2") }
            Button(onClick = { onApplyFactor(3.0) }) { Text("x3") }
            Button(onClick = { onApplyFactor(4.0) }) { Text("x4") }

            OutlinedButton(onClick = { onApplyFactor(1.0) }) {
                Text(stringResource(R.string.reset))
            }
        }

        Spacer(Modifier.height(12.dp))

        /**
         * Factor libre
         */
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {

                Text(
                    stringResource(R.string.free_factor),
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = ui.factorText,
                    onValueChange = onSetFactorText,
                    label = { Text(stringResource(R.string.factor_example)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(6.dp))

                Button(
                    onClick = {
                        ui.factorText.toDoubleOrNull()?.let {
                            onApplyFactor(it)
                        }
                    }
                ) {
                    Text(stringResource(R.string.apply))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        /**
         * Ingredientes escalados
         */
        Text(
            stringResource(R.string.ingredients),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            scaled?.let {
                items(it.ingredients) { ing ->
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

                /**
                 * Sección Elaboración
                 */
                Text(
                    stringResource(R.string.method),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(6.dp))

                Card(Modifier.fillMaxWidth()) {
                    Text(
                        text = recipe?.method ?: "",
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
