package com.ronaldveloz.recetas.ui.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun IngredientDropdown(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedButton(onClick = { expanded = true }, modifier = modifier) {
        Text(selected, modifier = Modifier.weight(1f))
        Text("▼")
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        options.forEach { opt ->
            DropdownMenuItem(
                text = { Text(opt) },
                onClick = { onSelected(opt); expanded = false }
            )
        }
    }
}
