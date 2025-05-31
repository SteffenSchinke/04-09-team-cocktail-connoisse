package de.syntax.institut.projectweek.cocktailconnoisse.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.schinke.steffen.enums.ShadowPosition
import de.schinke.steffen.ui.components.CostumAsyncImage
import de.schinke.steffen.ui.components.CostumShadowBox
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail

@Composable
internal fun CocktailItemDetail (

    cocktail: Cocktail,
) {

        Spacer(Modifier.height(20.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .padding(start = 6.dp)
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
                            .fillMaxWidth()
                            .height(250.dp),
                        url = cocktail.imageUrl
                    )

                    Row(

                        Modifier.fillMaxWidth()
                    ) {

                        TextWithShadow(
                            modifier = Modifier.weight(0.85f),
                            text = cocktail.name,
                            textStyle = MaterialTheme.typography.headlineLarge
                        )

                        FavoriteSwitch(
                            modifier = Modifier
                                .padding(12.dp)
                                .weight(0.15f),
                            cocktailId = cocktail.id,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
    }