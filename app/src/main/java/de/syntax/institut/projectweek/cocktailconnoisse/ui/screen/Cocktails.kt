package de.syntax.institut.projectweek.cocktailconnoisse.ui.screen

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
        get() = { viewModelMap, _, _, _, _, _ ->

            if (viewModelMap[CocktailsViewModel::class] != null &&
                viewModelMap.keys.contains(CocktailsViewModel::class)
            ) {

                val viewModel = viewModelMap[CocktailsViewModel::class] as CocktailsViewModel
                val apiError by viewModel.apiError.collectAsState()
                val viewModelState by viewModel.state.collectAsState()
                val randomCocktail by viewModel.randomCocktail.collectAsState()
                val randomCocktails by viewModel.randomCocktails.collectAsState()

                LaunchedEffect(Unit) {

                    viewModel.loadCocktails()
                }

                when (viewModelState) {

                    ViewModelState.READY -> {
                        Content(viewModel, randomCocktail, randomCocktails)
                    }

                    ViewModelState.WORKING -> {
                        CostumProgressCircle()
                    }

                    ViewModelState.ERROR -> {
                        viewModel.sendMessageOnSnackbar(
                            message = getStringResourceByName(apiError?.innerMessage ?: "api_error_unknown"),
                            messageCode = apiError?.responseCode?.toString(),
                            mode = SnackbarMode.ERROR,
                            duration = SnackbarDisplayTime.INDEFINITE
                        )
                        viewModel.resetApiError()
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
                            text = stringResource(R.string.screen_cocktails),
                            style = MaterialTheme.typography.headlineMedium
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
}

@Composable
private fun Content(
    viewModel: CocktailsViewModel,
    cocktail: Cocktail?,
    cocktails: List<Cocktail>
) {

    Column {

        Spacer(Modifier.height(50.dp))

        cocktail?.let {

            Text("Cocktail des Tages")

            CostumAsyncImage(Modifier.fillMaxWidth().height(250.dp), it.imageUrl ?: "")
        }

        Text("Cocktail Liste")

        LazyColumn {

            items(cocktails) {

                CostumAsyncImage(
                    url = it.imageUrl ?: "",
                    size = 120.dp)
            }
        }
    }
}