package de.syntax.institut.projectweek.cocktailconnoisse.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.schinke.steffen.enums.ShadowPosition
import de.schinke.steffen.ui.components.CostumAsyncImage
import de.schinke.steffen.ui.components.CostumShadowBox
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail

@Composable
fun CocktailItemMedium(

    cocktail: Cocktail,
    onNavigation: (Cocktail) -> Unit,
) {

    Box(
        Modifier.fillMaxWidth()
    ) {
        CostumShadowBox(
            elevation = 6.dp,
            shadowPositions = setOf(ShadowPosition.TOP, ShadowPosition.LEFT),
            cornerRadius = 12.dp,
            shadowColor = MaterialTheme.colorScheme.secondary
        ) {

            Box {
                CostumAsyncImage(
                    modifier = Modifier
                        .width(250.dp)
                        .height(250.dp)
                        .clickable { onNavigation(cocktail) },
                    url = cocktail.imageUrl
                )

                FavoriteSwitch(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    cocktail.id
                )
            }
        }

        TextWithShadow(
            text = cocktail.name,
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}