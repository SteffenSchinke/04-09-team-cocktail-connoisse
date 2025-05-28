package de.syntax.institut.projectweek.cocktailconnoisse.ui.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.CategoriesViewModel

@Composable
fun FilterSheet(viewModel: CategoriesViewModel, ingredient: String) {
    val cocktails by viewModel.cocktails.collectAsState()

    LaunchedEffect(ingredient) {
        viewModel.loadCocktailsByIngredient(ingredient)
    }

    LazyColumn {
        items(cocktails) { cocktail ->
            Text(text = cocktail.name)
            cocktail.ingredients.forEach { ingredient ->
                Text(text = "- ${ingredient.name}")
            }
        }
    }
}