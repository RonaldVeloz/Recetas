package com.ronaldveloz.recetas.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class IngredientDto(
    val name: String,
    val amount: Double,
    val unit: String
)

@Serializable
data class RecipeDto(
    val id: String,
    val name: String,
    val baseYieldAmount: Double,
    val baseYieldUnit: String,
    val ingredients: List<IngredientDto>,
    val method: String
)
