package de.syntax.institut.projectweek.cocktailconnoisse.ui.composable


import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.syntax.institut.projectweek.cocktailconnoisse.ui.theme.CocktailConnoisseTheme


@Composable
internal fun CostumTopBarBackground() {

    val color = MaterialTheme.colorScheme.secondary

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {

        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, height * 0.9f)
            quadraticTo(width / 5f, height * 1.7f,
                        width, height * 0.9f)
            lineTo(width, 0f)
            close()
        }

        drawPath(path = path, color = color)
    }
}

@Preview(showBackground = true, name = "Light")
@Preview(showBackground = true, name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CostumTopBarBackgroundPreview() {

    CocktailConnoisseTheme {
        CostumTopBarBackground()
    }
}