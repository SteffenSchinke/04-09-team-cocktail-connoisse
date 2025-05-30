package de.syntax.institut.projectweek.cocktailconnoisse.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.syntax.institut.projectweek.cocktailconnoisse.R
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.FavoriteSwitchViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
internal fun FavoriteSwitch(

    modifier: Modifier = Modifier,
    cocktailId: Long
) {

    val viewModel: FavoriteSwitchViewModel = koinViewModel(
        key = "favoriteSwitchViewModel_$cocktailId",
        parameters = { parametersOf(cocktailId) }
    )

    val cocktail by viewModel.cocktail.collectAsState()


    cocktail?.let {
        val painterOn = painterResource(R.drawable.png_favorite_on)
        val painterOff = painterResource(R.drawable.png_favorite_off)
        val descOn = stringResource(R.string.label_favorite_on)
        val descOff = stringResource(R.string.label_favorite_off)

        Box(
            modifier = modifier.clickable { viewModel.updateFavoriteState() }
        ) {
            if (it.favorited)
                Image(painterOn, contentDescription = descOn, Modifier.scale(1.2f))
            else
                Image(painterOff, contentDescription = descOff, Modifier.scale(1.2f))
        }
    }
}