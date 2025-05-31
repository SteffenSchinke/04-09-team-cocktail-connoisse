package de.syntax.institut.projectweek.cocktailconnoisse.ui.screen

import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import de.schinke.steffen.enums.ShadowPosition
import de.schinke.steffen.enums.SnackbarDisplayTime
import de.schinke.steffen.enums.SnackbarMode
import de.schinke.steffen.enums.ViewModelState
import de.schinke.steffen.extensions.sendMessageOnSnackbar
import de.schinke.steffen.interfaces.AppRoute
import de.schinke.steffen.interfaces.AppRouteContent
import de.schinke.steffen.interfaces.AppRouteSheet
import de.schinke.steffen.ui.components.CostumAsyncImage
import de.schinke.steffen.ui.components.CostumBackButton
import de.schinke.steffen.ui.components.CostumErrorImage
import de.schinke.steffen.ui.components.CostumProgressCircle
import de.schinke.steffen.ui.components.CostumShadowBox
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.extension.getStringResourceByName
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.CostumTopBarBackground
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.FavoriteSwitch
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.TextWithShadow
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.CocktailsViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.KClass

object Cocktails: AppRoute, AppRouteContent {

    override val route: String
        get() = "cocktails/{ids}?top_bar_title={top_bar_title}"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument("ids") {
            type = NavType.StringType
        },
        navArgument("top_bar_title") {
            type = NavType.StringType
        }
    )

    override val viewModelDependencies: Map<KClass<out ViewModel>, @Composable (() -> ViewModel)>
        get() = mapOf(CocktailsViewModel::class to { koinViewModel<CocktailsViewModel>() })


    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, SheetState, Bundle?, (AppRouteSheet, Bundle?) -> Unit, () -> Unit) -> Unit)?
        get() = { viewModelMap, navController, _, _, _, _ ->

            val viewModel =
                viewModelMap.getOrDefault(CocktailsViewModel::class, null) as CocktailsViewModel?
            viewModel?.let { viewModelCocktails ->

                val cocktails by viewModelCocktails.cocktails.collectAsState()
                val viewModelState by viewModelCocktails.state.collectAsState()
                val apiError by viewModelCocktails.repoError.collectAsState()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val stringIds= navBackStackEntry?.arguments?.getString("ids") ?: ""

                LaunchedEffect(stringIds) {
                    if (viewModelState == ViewModelState.READY) {
                        viewModelCocktails.setCocktailIds(stringIds.toString())
                    }
                }

                when (viewModelState) {

                    ViewModelState.READY -> {
                        Content(cocktails, navController)
                    }

                    ViewModelState.WORKING -> {
                        CostumProgressCircle()
                    }

                    ViewModelState.ERROR -> {
                        apiError?.let {
                            viewModelCocktails.sendMessageOnSnackbar(
                                message = getStringResourceByName(
                                    apiError?.innerMessage ?: "api_error_unknown"
                                ),
                                messageCode = apiError?.responseCode?.toString(),
                                mode = SnackbarMode.ERROR,
                                duration = SnackbarDisplayTime.INDEFINITE
                            )
                            viewModelCocktails.resetApiError()
                        }
                        CostumErrorImage()
                    }
                }
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override val topBar: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, (AppRouteSheet, Bundle?) -> Unit) -> Unit)?
        get() = { _, navController, _ ->

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val tabBarTitle =
                navBackStackEntry?.arguments?.getString("top_bar_title") ?: "screen_cocktails"

            Box {
                CostumTopBarBackground()
                TopAppBar(
                    title = {
                        Text(
                            text = getStringResourceByName(tabBarTitle),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    navigationIcon = {
                        CostumBackButton(
                            navController = navController,
                            tintColor = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                )
            }
        }

    override val fab: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, (AppRouteSheet, Bundle?) -> Unit) -> Unit)?
        get() = null

    @Composable
    private fun Content(
        cocktails: List<Cocktail>,
        navController: NavHostController
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
                            textStyle = MaterialTheme.typography.bodyLarge
                            )

                        Column(
                            Modifier.fillMaxSize()
                        ) {
                            Row(
                                Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.End
                            ) { FavoriteSwitch(Modifier.padding(8.dp), cocktail.id) }
                        }
                    }
                }
            }
        }
    }
}