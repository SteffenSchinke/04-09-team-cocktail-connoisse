package de.syntax.institut.projectweek.cocktailconnoisse.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.syntax.institut.projectweek.cocktailconnoisse.R
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.ui.screen.Details

@Composable
internal fun CocktailListByIngredient(
    cocktail: Cocktail,
    cocktails: List<Cocktail>,
    navController: NavHostController
) {

    if (cocktails.isEmpty()) return

    val ingredient = cocktail.ingredients.firstOrNull()?.name ?: "Unbekannte Zutat"

    Spacer(Modifier.height(12.dp))

    Column(
        Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {

        if(cocktails.count() == 0){

            Text(
                text = "Keine Cocktails mit der Zutat '$ingredient' gefunden",
                style = MaterialTheme.typography.bodySmall
            )
        } else {

            Text(
                text = stringResource(R.string.label_home_title2) + " mit '$ingredient'",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(5.dp)
            ) {
                items(cocktails) { cocktail ->

                    CocktailItemMedium(cocktail) {
                        navController.navigate(
                            Details.route.replace(
                                "{id}",
                                cocktail.id.toString()
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}