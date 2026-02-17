package com.ronaldveloz.recetas.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier


/**
 * Componente reutilizable para seleccionar un ingrediente.
 *
 * No contiene lógica de negocio.
 * Solo:
 * - Muestra la opción seleccionada
 * - Permite desplegar menú
 * - Notifica al padre cuando se selecciona uno nuevo
 */
@Composable
fun IngredientDropdown(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado local para saber si el menú está abierto o cerrado
    var expanded by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { expanded = true },
        modifier = modifier.fillMaxWidth()
    ) {

        Row(modifier = Modifier.fillMaxWidth()) {

            // Texto del ingrediente seleccionado
            Text(
                text = selected,
                modifier = Modifier.weight(1f)
            )

            // Icono estándar de desplegable
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Abrir lista de ingredientes"
            )
        }
    }

    // Menú desplegable
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        options.forEach { opt ->
            DropdownMenuItem(
                text = { Text(opt) },
                onClick = {
                    onSelected(opt)
                    expanded = false
                }
            )
        }
    }
}
