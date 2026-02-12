package com.ronaldveloz.recetas.data.mapper

import com.ronaldveloz.recetas.data.dto.IngredientDto
import com.ronaldveloz.recetas.data.dto.RecipeDto
import com.ronaldveloz.recetas.domain.model.Ingredient
import com.ronaldveloz.recetas.domain.model.Recipe
import com.ronaldveloz.recetas.domain.model.UnitType

// Mapeo DTO -> Domain
// Lo hago así para que el "domain" no dependa de JSON ni de Android.
fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        id = id,
        name = name,
        baseYieldAmount = baseYieldAmount,
        baseYieldUnit = UnitType.valueOf(baseYieldUnit),
        ingredients = ingredients.map { it.toDomain() },
        method = method
    )
}

fun IngredientDto.toDomain(): Ingredient {
    return Ingredient(
        name = name,
        amount = amount,
        unit = UnitType.valueOf(unit)
    )
}


