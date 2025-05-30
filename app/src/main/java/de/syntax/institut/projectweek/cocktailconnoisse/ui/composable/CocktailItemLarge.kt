package de.syntax.institut.projectweek.cocktailconnoisse.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import de.schinke.steffen.enums.ShadowPosition
import de.schinke.steffen.ui.components.CostumAsyncImage
import de.schinke.steffen.ui.components.CostumShadowBox
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.ui.screen.Details

@Composable
internal fun CocktailItemLarge (

    cocktail: Cocktail,
    navController: NavHostController
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
                            .height(250.dp)
                            .clickable {
                                navController.navigate(
                                    Details.route.replace("{id}", cocktail.id.toString())
                                )
                            },
                        url = cocktail.imageUrl
                    )

                    TextWithShadow(
                        text = cocktail.name,
                        fontSize = 16.sp
                    )

                    FavoriteSwitch(
                        modifier = Modifier
                            .padding(8.dp),
                        cocktailId = cocktail.id,
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
    }