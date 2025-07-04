package de.syntax.institut.projectweek.cocktailconnoisse.ui.screen

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import de.schinke.steffen.ui.components.CostumErrorImage
import de.schinke.steffen.ui.components.CostumProgressCircle
import de.syntax.institut.projectweek.cocktailconnoisse.R
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Cocktail
import de.syntax.institut.projectweek.cocktailconnoisse.extension.getStringResourceByName
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.CocktailItemLarge
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.CocktailListMedium
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.CostumTopBarBackground
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.HomeViewModel
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.KClass


object Home : AppRouteTab, AppRouteContent {

    override val tabTitle: String
        @Composable
        get() = stringResource(R.string.screen_home)

    override val tabIcon: @Composable (() -> Unit)
        get() = { Icon(Icons.Default.Home, null) }

    override val route: String
        get() = "home"

    override val viewModelDependencies: Map<KClass<out ViewModel>, @Composable (() -> ViewModel)>
        get() = mapOf(
            HomeViewModel::class to { koinViewModel<HomeViewModel>() },
            SettingsViewModel::class to { koinViewModel<SettingsViewModel>() }
        )


    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, SheetState, Bundle?, (AppRouteSheet, Bundle?) -> Unit, () -> Unit) -> Unit)?
        get() = { viewModelMap, navController, _, _, _, _ ->

            val viewModel =
                viewModelMap.getOrDefault(HomeViewModel::class, null) as HomeViewModel?
            viewModel?.let { viewModelHome ->

                val viewModelState by viewModelHome.state.collectAsState()
                val apiError by viewModelHome.repoError.collectAsState()
                val cocktail = viewModelHome.cocktail.collectAsState().value
                val cocktails = viewModelHome.cocktails.collectAsState().value

                LaunchedEffect(Unit) {

                    if (viewModelState == ViewModelState.READY) {

                        viewModelHome.loadCocktails()
                    }
                }

                when (viewModelState) {

                    ViewModelState.READY -> {

                        cocktail?.let {
                            Content(navController, it, cocktails, viewModelHome)
                        }
                    }

                    ViewModelState.WORKING -> {
                        CostumProgressCircle()
                    }

                    ViewModelState.ERROR -> {
                        apiError?.let {
                            viewModelHome.sendMessageOnSnackbar(
                                message = getStringResourceByName(
                                    apiError?.innerMessage ?: "api_error_unknown"
                                ),
                                messageCode = apiError?.responseCode?.toString(),
                                mode = SnackbarMode.ERROR,
                                duration = SnackbarDisplayTime.INDEFINITE
                            )
                            viewModelHome.resetApiError()
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
                viewModelMap.getOrDefault(HomeViewModel::class, null) as HomeViewModel?
            viewModel?.let { viewModelHome ->

                val withAlcoholic = viewModelHome.withAlcoholic.collectAsState().value


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
                                onCheckedChange = {
                                    viewModelHome.setCocktailType(it)
                                    if (viewModelHome.searchText.isNotBlank()) {
                                        viewModelHome.searchCocktailsByName(viewModelHome.searchText)
                                    }
                                }
                            )
                            IconButton(onClick = {
                                viewModel.isSearching = !viewModel.isSearching
                            }) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Suche",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    )
                }
            }
        }

    override val fab: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, (AppRouteSheet, Bundle?) -> Unit) -> Unit)?
        get() = null

    @Composable
    private fun Content(

        navController: NavHostController,
        cocktail: Cocktail,
        cocktails: List<Cocktail>,
        viewModelHome: HomeViewModel
    ) {
        val withAlcoholic by viewModelHome.withAlcoholic.collectAsState()
        Column {
            if (viewModelHome.isSearching) {
                Column {
                    TextField(
                        value = viewModelHome.searchText,
                        onValueChange = {
                            viewModelHome.searchText = it
                            viewModelHome.searchCocktailsByName(it)
                        },
                        placeholder = { Text(stringResource(R.string.label_search)) },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            viewModelHome.isSearching = false
                            viewModelHome.searchText = ""
                            viewModelHome.searchedCocktails.value = emptyList()
                        },
                        modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
                    ) {
                        Text(stringResource(R.string.label_close))
                    }
                }
                if (viewModelHome.searchText.isNotBlank()) {
                    if (viewModelHome.searchedCocktails.value.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val typeText = if (withAlcoholic) stringResource(R.string.label_mit_alkohol) else stringResource(R.string.label_ohne_alkohol)
                            Text(
                                text = stringResource(R.string.label_no_cocktails_found, typeText, viewModelHome.searchText),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn {
                            items(viewModelHome.searchedCocktails.value.filter { cocktail ->
                                if (viewModelHome.withAlcoholic.value) {
                                    cocktail.isAlcoholic
                                } else {
                                    !cocktail.isAlcoholic
                                }
                            }) { cocktail ->
                                CocktailItemLarge(cocktail, navController)
                            }
                        }
                    }
                } else {

                    Text(
                        text = stringResource(R.string.label_home_title1),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    CocktailItemLarge(cocktail, navController)

                    Spacer(Modifier.height(10.dp))

                    CocktailListMedium(cocktails, navController)
                }
            } else {

                Text(
                    text = stringResource(R.string.label_home_title1),
                    style = MaterialTheme.typography.headlineSmall
                )

                CocktailItemLarge(cocktail, navController)

                Spacer(Modifier.height(10.dp))

                CocktailListMedium(cocktails, navController)
            }
        }
    }
}

