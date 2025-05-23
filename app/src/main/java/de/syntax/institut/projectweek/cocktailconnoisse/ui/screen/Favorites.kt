package de.syntax.institut.projectweek.cocktailconnoisse.ui.screen

import android.os.Bundle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import de.schinke.steffen.interfaces.AppRouteContent
import de.schinke.steffen.interfaces.AppRouteSheet
import de.schinke.steffen.interfaces.AppRouteTab
import de.syntax.institut.projectweek.cocktailconnoisse.R
import kotlin.reflect.KClass

object Favorites: AppRouteTab, AppRouteContent {

    override val tabTitle: String
        @Composable
        get() = stringResource(R.string.screen_favorites)

    override val tabIcon: @Composable (() -> Unit)
        get() = { Icon(Icons.Default.Favorite, null) }

    override val route: String
        get() = "favorites"

    override val viewModelDependencies: Map<KClass<out ViewModel>, @Composable (() -> ViewModel)>
        get() = mapOf()

    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, SheetState, Bundle?, (AppRouteSheet, Bundle?) -> Unit, () -> Unit) -> Unit)?
        get() = { _, _, _, _, _, _ ->

        }

    @OptIn(ExperimentalMaterial3Api::class)
    override val topBar: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, (AppRouteSheet, Bundle?) -> Unit) -> Unit)?
        get() = { _, _, _ ->
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.screen_favorites),
                        style = MaterialTheme.typography.headlineMedium)
                }
            )
        }

    override val fab: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, (AppRouteSheet, Bundle?) -> Unit) -> Unit)?
        get() = null

}