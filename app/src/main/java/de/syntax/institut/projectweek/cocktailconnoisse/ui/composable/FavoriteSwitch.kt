package de.syntax.institut.projectweek.cocktailconnoisse.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.syntax.institut.projectweek.cocktailconnoisse.R
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail

@Composable
internal fun FavoriteSwitch(

    modifier: Modifier = Modifier,
    cocktail: Cocktail,
    onFavoriteChange: (Cocktail) -> Unit
) {

    val painterOn = painterResource(R.drawable.png_favorite_on)
    val painterOff = painterResource(R.drawable.png_favorite_off)
    val descOn = stringResource(R.string.label_favorite_on)
    val descOff = stringResource(R.string.label_favorite_off)

    Box(
        modifier = modifier
            .clickable { onFavoriteChange(cocktail) }
    ) {

        if (cocktail.favorited)
            Image( painterOn, descOn)
        else
            Image( painterOff, descOff)
    }
}