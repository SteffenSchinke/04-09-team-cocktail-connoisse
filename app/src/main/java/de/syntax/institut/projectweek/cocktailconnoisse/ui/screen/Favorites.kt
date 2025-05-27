package de.syntax.institut.projectweek.cocktailconnoisse.ui.screen

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import de.schinke.steffen.interfaces.AppRouteContent
import de.schinke.steffen.interfaces.AppRouteSheet
import de.schinke.steffen.interfaces.AppRouteTab
import de.syntax.institut.projectweek.cocktailconnoisse.R
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.CostumTopBarBackground
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.FavoritesViewModel
import kotlin.reflect.KClass

object Favorites : AppRouteTab, AppRouteContent {

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

            // TODO sts 25.05.25 content implement
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override val topBar: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, (AppRouteSheet, Bundle?) -> Unit) -> Unit)?
        get() = { viewModelMap, _, _ ->

            val viewModel =
                viewModelMap.getOrDefault(FavoritesViewModel::class, null) as FavoritesViewModel?
            viewModel?.let { viewModel ->

                val withAlcoholic by viewModel.withAlcoholic.collectAsState()

                Box {
                    CostumTopBarBackground()
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.screen_favorites),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        ),
                        actions = {

                            Text(
                                text = if (withAlcoholic) {
                                    stringResource(R.string.label_with_alcohol)
                                } else {
                                    stringResource(R.string.label_without_alcohol)

                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(Modifier.width(10.dp))
                            Switch(
                                checked = withAlcoholic,
                                onCheckedChange = viewModel::setCocktailType
                            )
                        }
                    )
                }
            }
        }

    override val fab: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, (AppRouteSheet, Bundle?) -> Unit) -> Unit)?
        get() = null

}