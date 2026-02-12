package com.ronaldveloz.recetas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ronaldveloz.recetas.data.RecipeRepository
import com.ronaldveloz.recetas.domain.usecase.ScaleRecipeUseCase
import com.ronaldveloz.recetas.viewmodel.RecipeDetailViewModel



class RecipeListVmFactory(
    private val repo: RecipeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecipeListViewModel(repo) as T
    }
}

class RecipeDetailVmFactory(
    private val repo: RecipeRepository,
    private val scaler: ScaleRecipeUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecipeDetailViewModel(repo, scaler) as T
    }
}


