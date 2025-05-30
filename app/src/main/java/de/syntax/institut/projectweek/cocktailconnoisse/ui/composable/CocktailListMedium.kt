package de.syntax.institut.projectweek.cocktailconnoisse.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.syntax.institut.projectweek.cocktailconnoisse.R
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.ui.screen.Cocktails
import de.syntax.institut.projectweek.cocktailconnoisse.ui.screen.Details

@Composable
internal fun CocktailListMedium(
    cocktails: List<Cocktail>,
    navController: NavHostController
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.label_home_title2),
            style = MaterialTheme.typography.headlineSmall
        )

        TextButton(
            onClick = {

                val route = Cocktails.route
                    .replace("{ids}", cocktails
                        .shuffled()
                        .take(40)
                        .joinToString(",") { it.id.toString() })
                    .replace("{top_bar_title}", "sheet_suggestions")
                navController.navigate(route)
            },
            content = { Text(stringResource(R.string.label_home_title3)) }
        )
    }

    Spacer(Modifier.height(10.dp))

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