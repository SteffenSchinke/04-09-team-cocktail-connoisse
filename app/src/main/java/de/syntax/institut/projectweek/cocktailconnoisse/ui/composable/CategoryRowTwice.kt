package de.syntax.institut.projectweek.cocktailconnoisse.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.schinke.steffen.enums.ShadowPosition
import de.schinke.steffen.ui.components.CostumShadowBox
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category

@Composable
fun CategoryRowTwice(

    categoryOne: Category,
    categoryTwo: Category,
    onNavigation: (Category) -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    )
    {
        Box(
            Modifier
                .weight(1f)
                .padding(start = 6.dp)
        ) {
            CostumShadowBox(
                Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth(),
                elevation = 6.dp,
                shadowPositions = setOf(ShadowPosition.TOP, ShadowPosition.LEFT),
                cornerRadius = 20.dp,
                shadowColor = MaterialTheme.colorScheme.secondary
            ) {
                Image(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = { onNavigation(categoryOne) }),
                    painter = painterResource(id = categoryOne.imageId),
                    contentDescription = categoryOne.name
                )
            }
            TextWithShadow(
                text = categoryOne.name,
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.width(30.dp))

        Box(
            Modifier
                .weight(1f)
                .padding(start = 6.dp)
        ) {
            CostumShadowBox(
                Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth(),
                elevation = 6.dp,
                shadowPositions = setOf(ShadowPosition.TOP, ShadowPosition.LEFT),
                cornerRadius = 20.dp,
                shadowColor = MaterialTheme.colorScheme.secondary
            ) {
                Image(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = { onNavigation(categoryTwo) }),

                    painter = painterResource(id = categoryTwo.imageId),
                    contentDescription = categoryTwo.name
                )
            }
            TextWithShadow(
                text = categoryTwo.name,
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }
    }
}