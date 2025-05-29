package de.syntax.institut.projectweek.cocktailconnoisse.ui.composable

import androidx.compose.runtime.Composable
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.CategoriesViewModel

@Composable
fun FilterSheet(viewModel: CategoriesViewModel, ingredient: String) {

// TODO sts 29.05.25 refactoring
//
//    val cocktails by viewModel.cocktails.collectAsState()
//
//    LaunchedEffect(ingredient) {
//        viewModel.loadCocktailsByIngredient(ingredient)
//    }
//
//    LazyColumn {
//        items(cocktails) { cocktail ->
//            Text(text = cocktail.name)
//            cocktail.ingredients.forEach { ingredient ->
//                Text(text = "- ${ingredient.name}")
//            }
//        }
//    }
}