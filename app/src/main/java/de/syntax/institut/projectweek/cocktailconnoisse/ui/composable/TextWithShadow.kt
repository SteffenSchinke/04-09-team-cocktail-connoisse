package de.syntax.institut.projectweek.cocktailconnoisse.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextWithShadow(
    text: String,
    fontSize: TextUnit = 24.sp,
    strokeColor: Color = Color.Black,
    strokeWidth: Float = 2f,
    textColor: Color = Color.White
) {
    Box(modifier = Modifier.padding(8.dp)) {
        val offsets = listOf(
            Offset(-strokeWidth, -strokeWidth),
            Offset(-strokeWidth, strokeWidth),
            Offset(strokeWidth, -strokeWidth),
            Offset(strokeWidth, strokeWidth)
        )

        offsets.forEach { offset ->
            Text(
                text = text,
                fontSize = fontSize,
                color = strokeColor,
                modifier = Modifier.offset(offset.x.dp, offset.y.dp)
            )
        }

        Text(
            text = text,
            fontSize = fontSize,
            color = textColor
        )
    }
}