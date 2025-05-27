package de.syntax.institut.projectweek.cocktailconnoisse.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import de.schinke.steffen.ui.components.DefaultLaunch
import de.syntax.institut.projectweek.cocktailconnoisse.R

@Composable
fun Launch() {

    Box {

        Image(
            painter = painterResource(R.drawable.coktails),
            contentDescription = "Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        DefaultLaunch()
    }
}