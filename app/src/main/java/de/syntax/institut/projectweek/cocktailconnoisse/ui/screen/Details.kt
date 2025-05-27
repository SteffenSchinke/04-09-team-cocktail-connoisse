package de.syntax.institut.projectweek.cocktailconnoisse.ui.screen

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
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
import de.syntax.institut.projectweek.cocktailconnoisse.R
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.extension.getStringResourceByName
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.CostumTopBarBackground
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.DetailsViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.KClass

object Details : AppRoute, AppRouteContent {
    override val route: String
        get() = "details/{id}"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument("id") {
            type = NavType.StringType
        }
    )

    override val viewModelDependencies: Map<KClass<out ViewModel>, @Composable (() -> ViewModel)>
        get() = mapOf(
            DetailsViewModel::class to { koinViewModel<DetailsViewModel>() }
        )

    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable ((
        Map<KClass<out ViewModel>, ViewModel>, NavHostController,
        SheetState, Bundle?, (AppRouteSheet, Bundle?) -> Unit, () -> Unit
    ) -> Unit)?
        get() = { viewModelMap, navController, _, _, _, _ ->

            val viewModel =
                viewModelMap.getOrDefault(DetailsViewModel::class, null) as DetailsViewModel?
            viewModel?.let { viewModel ->

                val viewModelState by viewModel.state.collectAsState()
                val apiError by viewModel.apiError.collectAsState()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val id = navBackStackEntry?.arguments?.getString("id") ?: ""

                LaunchedEffect(id) {

                    if (viewModelState == ViewModelState.READY && !id.isEmpty()) {
                        viewModel.loadCocktailById(id)
                    }
                }

                when (viewModelState) {
                    ViewModelState.READY -> {
                        Content(viewModel)
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
    override val topBar: @Composable ((
        Map<KClass<out ViewModel>, ViewModel>, NavHostController,
        (AppRouteSheet, Bundle?) -> Unit
    ) -> Unit)?
        get() = { _, navController, _ ->

            Box {
                CostumTopBarBackground()
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.screen_details),
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

        viewModel: DetailsViewModel
    ) {

        val cocktail by viewModel.cocktail.collectAsState()
        val isFavorited by viewModel.isFavorited.collectAsState()

        Column(Modifier.fillMaxSize()) {

            cocktail?.let {

                Log.d("Details", "Content: $it")

                Text(
                    text = it.name,
                    style = MaterialTheme.typography.headlineMedium
                )

                CostumAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    url = it.imageUrl
                )

                IconButton(
                    onClick = viewModel::updateIsFavorited,
                    content = {
                        if (isFavorited)
                            Icon(painterResource(R.drawable.ic_favorite_on), "Favorite On")
                        else
                            Icon(painterResource(R.drawable.ic_favorite_off), "Favorite Off")
                    }
                )
            }


            // TODO sts 25.05.25 content implement

            // TODO sts 24.05.25 - list horizontal cocktails from self type
        }
    }
}