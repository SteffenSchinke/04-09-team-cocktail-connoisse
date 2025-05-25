package de.syntax.institut.projectweek.cocktailconnoisse.ui.screen

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
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
import de.syntax.institut.projectweek.cocktailconnoisse.R
import de.syntax.institut.projectweek.cocktailconnoisse.data.external.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.extension.getStringResourceByName
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.CostumTopBarBackground
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.CocktailsViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.KClass

object Cocktails : AppRouteTab, AppRouteContent {

    override val tabTitle: String
        @Composable
        get() = stringResource(R.string.screen_cocktails)

    override val tabIcon: @Composable (() -> Unit)
        get() = { Icon(Icons.Default.LocalBar, null) }

    override val route: String
        get() = "cocktails"

    override val viewModelDependencies: Map<KClass<out ViewModel>, @Composable (() -> ViewModel)>
        get() = mapOf(
            CocktailsViewModel::class to { koinViewModel<CocktailsViewModel>() }
        )

    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, SheetState, Bundle?, (AppRouteSheet, Bundle?) -> Unit, () -> Unit) -> Unit)?
        get() = { viewModelMap, navController, _, _, _, _ ->

            val viewModel =
                viewModelMap.getOrDefault(CocktailsViewModel::class, null) as CocktailsViewModel?
            viewModel?.let { viewModel ->

                val viewModelState by viewModel.state.collectAsState()
                val apiError by viewModel.apiError.collectAsState()
                val randomCocktail by viewModel.randomCocktail.collectAsState()
                val randomCocktails by viewModel.randomCocktails.collectAsState()

                LaunchedEffect(Unit) {

                    if (viewModelState == ViewModelState.READY) {

                        viewModel.loadCocktails()
                    }
                }

                when (viewModelState) {

                    ViewModelState.READY -> {
                        Content(viewModel, navController, randomCocktail, randomCocktails)
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
        get() = { viewModelMap, _, _ ->

            val viewModel =
                viewModelMap.getOrDefault(CocktailsViewModel::class, null) as CocktailsViewModel?
            viewModel?.let { viewModel ->

                val withAlcoholic by viewModel.withAlcoholic.collectAsState()

                Box {
                    CostumTopBarBackground()
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.screen_cocktails),
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
                                    stringResource(R.string.lable_with_alcohol)
                                } else {
                                    stringResource(R.string.lable_without_alcohol)

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

@Composable
private fun Content(
    viewModel: CocktailsViewModel,
    navController: NavHostController,
    cocktail: Cocktail?,
    cocktails: List<Cocktail>
) {

    Column(Modifier.fillMaxSize()) {

        cocktail?.let {

            Log.d("Cocktails", "Cocktail: $cocktail")

            Text(
                text = "Cocktail Empfehlung",
                style = MaterialTheme.typography.headlineSmall
            )

            CostumAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clickable(onClick = {
                        navController.navigate(Details.route.replace("{id}", it.id))
                    }),
                url = it.imageUrl ?: ""
            )
        }

        Text(
            text = "Cocktail Vorschläge",
            style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn {

            items(cocktails) {

                Log.d("Cocktails", "Cocktail item: $it")

                CostumAsyncImage(
                    modifier = Modifier
                        .clickable(onClick = {
                            navController.navigate(Details.route.replace("{id}", it.id))
                        }),
                    url = it.imageUrl ?: "",
                    size = 120.dp
                )
            }
        }
    }
}