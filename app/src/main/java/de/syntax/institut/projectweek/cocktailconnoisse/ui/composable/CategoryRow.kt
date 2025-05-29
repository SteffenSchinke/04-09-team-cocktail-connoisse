package de.syntax.institut.projectweek.cocktailconnoisse.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.schinke.steffen.enums.ShadowPosition
import de.schinke.steffen.ui.components.CostumShadowBox
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category

@Composable
internal fun CategoryRow (

    category: Category,
    onNavigation: (Category) -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 6.dp)
            .padding(bottom = 30.dp, top = 10.dp)
    ) {

        CostumShadowBox(
            elevation = 6.dp,
            shadowPositions = setOf(ShadowPosition.TOP, ShadowPosition.LEFT),
            cornerRadius = 20.dp,
            shadowColor = MaterialTheme.colorScheme.secondary
        ) {
            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .fillMaxWidth()
                    .clickable(onClick = { onNavigation(category) }),
                painter = painterResource(id = category.imageId),
                contentDescription = category.name
            )
        }

        TextWithShadow(
            text = category.name,
            fontSize = 16.sp
        )
    }
}