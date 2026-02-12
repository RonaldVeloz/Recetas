package com.ronaldveloz.recetas.data
import com.ronaldveloz.recetas.domain.model.Recipe
import com.ronaldveloz.recetas.domain.model.Ingredient
import com.ronaldveloz.recetas.domain.model.UnitType



class InMemoryRecipeRepository : RecipeRepository{
    private val recipes = listOf(
        Recipe(
            id = "miel-mostaza",
            name = "Miel Mostaza",
            baseYieldAmount = 1300.0,
            baseYieldUnit = UnitType.G,
            ingredients = listOf(
                Ingredient(name = "Miel de flores", amount = 400.0, unit = UnitType.G),
                Ingredient(name = "Mostaza", amount = 200.0, unit = UnitType.G),
                Ingredient(name = "Vinagre de arroz", amount = 200.0, unit = UnitType.G),
            ),
            method = """
                1) Mezcla la miel con la mostaza hasta que quede homogéneo.
                2) Añade el vinagre de arroz y mezcla bien.
                3) Incorpora el aceite en hilo fino mientras bates para emulsionar.
                4) Ajusta al gusto.
                5) Guarda en tarro limpio en nevera.
            """.trimIndent()
        ),
        Recipe(
            id = "tempura",
            name = "Tempura",
            baseYieldAmount = 1850.0,
            baseYieldUnit = UnitType.G,
            ingredients = listOf(
                Ingredient("Tempurako", 850.0, UnitType.G),
                Ingredient("Agua", 1000.0, UnitType.G)
            ),
            method = """
                1) Enfriar bien el agua.
                2) Mezclar ligeramente el tempurako con el agua fría.
                3) No sobrebatir: la mezcla debe quedar irregular.
                4) Usar inmediatamente.
            """.trimIndent()
        ),
        Recipe(
            id = "morenito",
            name = "Morenito",
            baseYieldAmount = 1085.0,
            baseYieldUnit = UnitType.G,
            ingredients = listOf(
                Ingredient("Atún fresco", 1000.0, UnitType.G),
                Ingredient("Cebollino", 10.0, UnitType.G),
                Ingredient("Sriracha", 70.0, UnitType.G),
                Ingredient("Aceite de sésamo", 5.0, UnitType.G)
            ),
            method = """
                1) Separar la carne magra del atún de las fibras usando una cuchara.
                2) Mezclar el atún con el aceite de sésamo.
                3) Añadir cebollino y sriracha.
                4) Mezclar suave y mantener frío.
            """.trimIndent()
        ),
        Recipe(
            id = "maremoto",
            name = "Maremoto",
            baseYieldAmount = 1085.0,
            baseYieldUnit = UnitType.G,
            ingredients = listOf(
                Ingredient("Salmón", 1000.0, UnitType.G),
                Ingredient("Cebollino", 10.0, UnitType.G),
                Ingredient("Sriracha", 70.0, UnitType.G),
                Ingredient("Aceite de sésamo", 5.0, UnitType.G)
            ),
            method = """
                1) Cortar el salmón muy picado tipo tataki.
                2) Mezclar en un bol el salmón con el aceite de sésamo.
                3) Añadir sriracha y cebollino.
                4) Mezclar suave y mantener frío.
            """.trimIndent()
        ),
        Recipe(
            id = "salsa-shibuya",
            name = "Salsa Shibuya",
            baseYieldAmount = 980.0,
            baseYieldUnit = UnitType.G,
            ingredients = listOf(
                Ingredient("Leche condensada", 400.0, UnitType.G),
                Ingredient("Mayonesa japonesa", 500.0, UnitType.G),
                Ingredient("Zumo de naranja", 80.0, UnitType.G)
            ),
            method = """
                1) Mezclar la mayonesa japonesa con la leche condensada.
                2) Añadir el zumo de naranja poco a poco.
                3) Ajustar textura y mantener refrigerada.
            """.trimIndent()
        )

    )

    override fun getAll(): List<Recipe> = recipes
    override fun getById(id: String): Recipe? = recipes.firstOrNull {it.id == id}


            
}