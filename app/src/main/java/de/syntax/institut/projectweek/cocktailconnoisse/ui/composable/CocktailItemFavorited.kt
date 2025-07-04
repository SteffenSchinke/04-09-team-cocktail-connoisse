package de.syntax.institut.projectweek.cocktailconnoisse.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.schinke.steffen.enums.ShadowPosition
import de.schinke.steffen.ui.components.CostumAsyncImage
import de.schinke.steffen.ui.components.CostumShadowBox
import de.syntax.institut.projectweek.cocktailconnoisse.R
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail

@Composable
internal fun CocktailItemFavorited(

    cocktail: Cocktail,
    onNavigation: (Cocktail) -> Unit,
    onUpdateFavoriteState: (Cocktail) -> Unit
) {

    Box(
        Modifier
            .aspectRatio(1f)
            .padding(12.dp)
    ) {

        CostumShadowBox(
            elevation = 6.dp,
            shadowPositions = setOf(
                ShadowPosition.TOP,
                ShadowPosition.LEFT
            ),
            cornerRadius = 12.dp,
            shadowColor = MaterialTheme.colorScheme.secondary
        ) {

            CostumAsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onNavigation(cocktail) },
                url = cocktail.imageUrl
            )
        }
        TextWithShadow(
            text = cocktail.name,
            textStyle = MaterialTheme.typography.bodyLarge
        )

        Column(
            Modifier.fillMaxSize()
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {

                val painterOn = painterResource(R.drawable.png_favorite_on)
                val painterOff = painterResource(R.drawable.png_favorite_off)
                val descOn = stringResource(R.string.label_favorite_on)
                val descOff = stringResource(R.string.label_favorite_off)

                Box(
                    modifier = Modifier.clickable { onUpdateFavoriteState(cocktail) }
                ) {
                    if (cocktail.favorited)
                        Image(painterOn, contentDescription = descOn, Modifier.scale(1.2f))
                    else
                        Image(painterOff, contentDescription = descOff, Modifier.scale(1.2f))
                }
            }
        }
    }
}