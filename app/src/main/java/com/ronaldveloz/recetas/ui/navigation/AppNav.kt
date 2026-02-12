package com.ronaldveloz.recetas.ui.navigation

// Navigation (Compose)
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Data / Domain
import com.ronaldveloz.recetas.data.JsonAssetRecipeRepository
import com.ronaldveloz.recetas.data.RecipeRepository
import com.ronaldveloz.recetas.domain.usecase.ScaleRecipeUseCase

// UI screens
import com.ronaldveloz.recetas.ui.screens.RecipeDetailScreen
import com.ronaldveloz.recetas.ui.screens.RecipeListScreen

// ViewModels + Factories (inyección manual por ahora)
import com.ronaldveloz.recetas.viewmodel.RecipeDetailVmFactory
import com.ronaldveloz.recetas.viewmodel.RecipeDetailViewModel
import com.ronaldveloz.recetas.viewmodel.RecipeListVmFactory
import com.ronaldveloz.recetas.viewmodel.RecipeListViewModel

/**
 * Definimos rutas como constantes para evitar strings mágicos por todo el código.
 * - LIST: pantalla principal con el listado
 * - DETAIL: pantalla de detalle (recibe el id de la receta por parámetro)
 */
private const val ROUTE_LIST = "list"
private const val ROUTE_DETAIL = "detail/{id}"

/**
 * Este Composable es el "router" principal de la app.
 * Aquí decidimos qué pantalla se muestra en función de la ruta actual.
 *
 * Nota: todavía NO estamos usando Hilt/DI. Por ahora hacemos "inyección manual":
 * - Creamos el repositorio y el usecase aquí (una sola vez) y se lo pasamos a los ViewModels.
 * - Más adelante, esto se migra fácil a Hilt sin tocar la UI.
 */
@Composable
fun AppNav() {
    // Controlador de navegación de Compose
    val navController = rememberNavController()

    // Contexto de Android (lo necesitamos para leer el JSON desde assets)
    val context = LocalContext.current

    /**
     * Repositorio de recetas:
     * - Lee recipes.json desde app/src/main/assets/
     * - Cachea en memoria para no leer el fichero en cada llamada.
     *
     * OJO: uso applicationContext para evitar “fugas” del Activity.
     */
    val repo: RecipeRepository = remember {
        JsonAssetRecipeRepository(context.applicationContext)
    }

    /**
     * UseCase con la lógica de escalado (x2, x3, ajustar por ingrediente, etc).
     * Lo mantenemos fuera de la UI y de los ViewModels para respetar Clean Architecture.
     */
    val scaler = remember { ScaleRecipeUseCase() }

    /**
     * NavHost: define el grafo de navegación.
     * startDestination es la primera pantalla que se muestra.
     */
    NavHost(
        navController = navController,
        startDestination = ROUTE_LIST
    ) {

        /**
         * Pantalla LISTADO
         */
        composable(route = ROUTE_LIST) {

            // Creamos el VM con factory porque necesita dependencias (repo)
            val vm: RecipeListViewModel = viewModel(
                factory = RecipeListVmFactory(repo)
            )

            // Observamos el estado del VM como State para recomponer la UI automáticamente
            val ui by vm.ui.collectAsState()

            // Cargamos datos una única vez al entrar en la pantalla
            LaunchedEffect(Unit) {
                vm.load()
            }

            // Render UI: la pantalla no sabe nada de repositorios, solo consume state y callbacks
            RecipeListScreen(
                ui = ui,
                onQueryChange = vm::setQuery, // buscador: actualiza la query y el filtrado
                onRecipeClick = { recipe ->
                    // Navegamos al detalle pasando el id como parte de la ruta
                    navController.navigate("detail/${recipe.id}")
                }
            )
        }

        /**
         * Pantalla DETALLE
         * Recibe un argumento: "id"
         */
        composable(
            route = ROUTE_DETAIL,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            // Recuperamos el parámetro "id" que venía en la ruta
            val id = backStackEntry.arguments?.getString("id").orEmpty()

            // Creamos el VM con factory porque necesita repo + scaler
            val vm: RecipeDetailViewModel = viewModel(
                factory = RecipeDetailVmFactory(repo, scaler)
            )

            // Observamos el estado del VM
            val ui by vm.ui.collectAsState()

            // Cada vez que cambia el "id" (es decir, entramos a otra receta), recargamos
            LaunchedEffect(id) {
                vm.load(id)
            }

            // Render UI del detalle. La pantalla solo pinta y lanza eventos.
            RecipeDetailScreen(
                ui = ui,
                onBack = { navController.popBackStack() }, // volver atrás

                // Acciones rápidas (x2/x3/x4 y factor libre)
                onApplyFactor = vm::applyFactor,
                onSetFactorText = vm::setFactorText,

                // Modo “ajustar por ingrediente”
                onToggleChangeMode = vm::toggleChangeMode,
                onSelectIngredient = vm::setSelectedIngredient,
                onSetIngredientAmountText = vm::setIngredientAmountText,
                onRecalcByIngredient = vm::recalcByIngredient
            )
        }
    }
}
