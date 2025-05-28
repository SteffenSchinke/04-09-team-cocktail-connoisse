package de.syntax.institut.projectweek.cocktailconnoisse.ui.screen

import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import de.schinke.steffen.enums.ShadowPosition
import de.schinke.steffen.enums.SnackbarDisplayTime
import de.schinke.steffen.enums.SnackbarMode
import de.schinke.steffen.enums.ViewModelState
import de.schinke.steffen.extensions.sendMessageOnSnackbar
import de.schinke.steffen.interfaces.AppRouteContent
import de.schinke.steffen.interfaces.AppRouteSheet
import de.schinke.steffen.interfaces.AppRouteTab
import de.schinke.steffen.ui.components.CostumAsyncImage
import de.schinke.steffen.ui.components.CostumErrorImage
import de.schinke.steffen.ui.components.CostumProgressCircle
import de.schinke.steffen.ui.components.CostumShadowBox
import de.syntax.institut.projectweek.cocktailconnoisse.R
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.extension.getStringResourceByName
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.CostumTopBarBackground
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.TextWithShadow
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.FavoritesViewModel
import org.koin.androidx.compose.koinViewModel
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
        get() = mapOf(FavoritesViewModel::class to { koinViewModel<FavoritesViewModel>() })

    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, SheetState, Bundle?, (AppRouteSheet, Bundle?) -> Unit, () -> Unit) -> Unit)?
        get() = { viewModelMap, navController, _, _, _, _ ->
            val viewModel =
                viewModelMap.getOrDefault(FavoritesViewModel::class, null) as FavoritesViewModel?
            viewModel?.let { viewModelFavorite ->

                val viewModelState by viewModelFavorite.state.collectAsState()
                val apiError by viewModelFavorite.apiError.collectAsState()
                val cocktails = viewModelFavorite.cocktails.collectAsState()

                when (viewModelState) {

                    ViewModelState.READY -> {
                        Content(navController, cocktails.value)
                    }

                    ViewModelState.WORKING -> {
                        CostumProgressCircle()
                    }

                    ViewModelState.ERROR -> {
                        apiError?.let {
                            viewModelFavorite.sendMessageOnSnackbar(
                                message = getStringResourceByName(
                                    apiError?.innerMessage ?: "api_error_unknown"
                                ),
                                messageCode = apiError?.responseCode?.toString(),
                                mode = SnackbarMode.ERROR,
                                duration = SnackbarDisplayTime.INDEFINITE
                            )
                            viewModelFavorite.resetApiError()
                        }
                        CostumErrorImage()
                    }
                }
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override val topBar: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, (AppRouteSheet, Bundle?) -> Unit) -> Unit)?
        get() = { _, _, _ ->

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
                    )
                )
            }
        }


    override val fab: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, (AppRouteSheet, Bundle?) -> Unit) -> Unit)?
        get() = null

    @Composable
    private fun Content(
        navController: NavHostController,
        cocktails: List<Cocktail>
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            LazyVerticalGrid(

                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(cocktails) { cocktail ->

                    Box(
                        Modifier
                            .aspectRatio(1f)
                            .padding(12.dp)
                    ) {

                        CostumShadowBox(
                            elevation = 6.dp,
                            shadowPositions = setOf(
                                ShadowPosition.TOP,
                                ShadowPosition.LEFT
                            ),
                            cornerRadius = 12.dp,
                            shadowColor = MaterialTheme.colorScheme.secondary
                        ) {

                            CostumAsyncImage(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        navController.navigate(
                                            Details.route.replace(
                                                "{id}",
                                                cocktail.id.toString()
                                            )
                                        )
                                    },
                                url = cocktail.imageUrl
                            )
                        }
                        TextWithShadow(
                            text = cocktail.name,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}