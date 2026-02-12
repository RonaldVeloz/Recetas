package com.ronaldveloz.recetas.domain.model

data class Recipe(
    val id: String,
    val name: String,
    val baseYieldAmount: Double,
    val baseYieldUnit: UnitType,
    val ingredients: List<Ingredient>,
    val method: String
)




