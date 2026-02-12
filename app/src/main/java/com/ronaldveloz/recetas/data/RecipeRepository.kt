package com.ronaldveloz.recetas.data

import com.ronaldveloz.recetas.domain.model.Recipe


interface RecipeRepository{
    fun getAll(): List<Recipe>
    fun getById(id: String): Recipe?

}