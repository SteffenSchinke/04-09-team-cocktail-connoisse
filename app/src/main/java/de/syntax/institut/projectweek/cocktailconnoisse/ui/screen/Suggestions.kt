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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
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
import de.syntax.institut.projectweek.cocktailconnoisse.R
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.extension.getStringResourceByName
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.CostumTopBarBackground
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.SuggestionsViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.KClass

object Suggestions: AppRoute, AppRouteContent {

    override val route: String
        get() = "suggestions/{cocktail_type}"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument("cocktail_type") {
            type = NavType.StringType
        }
    )

    override val viewModelDependencies: Map<KClass<out ViewModel>, @Composable (() -> ViewModel)>
        get() = mapOf(
            SuggestionsViewModel::class to { koinViewModel<SuggestionsViewModel>() }
        )

    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, SheetState, Bundle?, (AppRouteSheet, Bundle?) -> Unit, () -> Unit) -> Unit)?
        get() = { viewModelMap, navController, _, _, _, _ ->

            val viewModel =
                viewModelMap.getOrDefault(SuggestionsViewModel::class, null) as SuggestionsViewModel?
            viewModel?.let { viewModel ->


                val viewModelState by viewModel.state.collectAsState()
                val apiError by viewModel.apiError.collectAsState()
                val cocktails = viewModel.cocktails.collectAsState().value

                LaunchedEffect(Unit) {

                    if (viewModelState == ViewModelState.READY) {

                        viewModel.loadCocktails()
                    }
                }

                when (viewModelState) {

                    ViewModelState.READY -> {
                        Content(navController, cocktails)
                    }

                    ViewModelState.WORKING -> {
                        CostumProgressCircle()
                    }

                    ViewModelState.ERROR -> {
                        apiError?.let {
                            viewModel.sendMessageOnSnackbar(
                                message = getStringResourceByName(
                                    apiError?.innerMessage ?: "api_error_unknown"
                                ),
                                messageCode = apiError?.responseCode?.toString(),
                                mode = SnackbarMode.ERROR,
                                duration = SnackbarDisplayTime.INDEFINITE
                            )
                            viewModel.resetApiError()
                        }
                        CostumErrorImage()
                    }
                }
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override val topBar: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, (AppRouteSheet, Bundle?) -> Unit) -> Unit)?
        get() = { _, navController, _ ->

            Box {
                CostumTopBarBackground()
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.sheet_suggestions),
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
    private fun Content(navController: NavHostController, cocktails: List<Cocktail>) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cocktails) { cocktailItem ->
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
                                                cocktailItem.id.toString()
                                            )
                                        )
                                    },
                                url = cocktailItem.imageUrl
                            )
                        }
                        Text(
                            text = cocktailItem.name,
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}