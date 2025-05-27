package de.syntax.institut.projectweek.cocktailconnoisse.ui.screen

import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
                val apiError by viewModelHome.apiError.collectAsState()
                val cocktail = viewModelHome.randomCocktail.collectAsState().value
                val cocktails = viewModelHome.randomCocktails.collectAsState().value
                val updatedCocktail = viewModelHome.updatedCocktail.collectAsState()

                LaunchedEffect(Unit, updatedCocktail) {

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
                                onCheckedChange = viewModelHome::setCocktailType
                            )
                        }
                    )
                }
            }
        }

    override val fab: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, (AppRouteSheet, Bundle?) -> Unit) -> Unit)?
        get() = null

    @Composable
    internal fun Content(

        navController: NavHostController,
        cocktail: Cocktail,
        cocktails: List<Cocktail>,
        homeViewModel: HomeViewModel
    ) {

        Column {

                CocktailItem(cocktail, navController, homeViewModel)

                Spacer(Modifier.height(10.dp))

                CocktailList(cocktails, navController, homeViewModel)
        }
    }

    @Composable
    private fun CocktailItem(cocktail: Cocktail, navController: NavHostController, viewModel: HomeViewModel) {

        Text(
            text = stringResource(R.string.label_home_title1),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(20.dp))

        Box(
            Modifier.fillMaxWidth().padding(start = 6.dp)
        ) {
            CostumShadowBox(
                elevation = 6.dp,
                shadowPositions = setOf(ShadowPosition.TOP, ShadowPosition.LEFT),
                cornerRadius = 12.dp,
                shadowColor = MaterialTheme.colorScheme.secondary
            ) {
                Box {
                    CostumAsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clickable {
                                navController.navigate(
                                    Details.route.replace("{id}", cocktail.id.toString())
                                )
                            },
                        url = cocktail.imageUrl
                    )
                    IconButton(
                        onClick = {
                            viewModel.updateFavorite(cocktail)
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.updateFavorite(cocktail)},
                            content = {
                                if (cocktail.favorited)
                                    Icon(painterResource(R.drawable.ic_favorite_on), "Favorite On")
                                else
                                    Icon(painterResource(R.drawable.ic_favorite_off), "Favorite Off")
                            }
                        )
                    }
                }

            }

            TextWithShadow(
                text = cocktail.name,
                fontSize = 16.sp
            )
        }
    }

    @Composable
    private fun CocktailList(cocktails: List<Cocktail>, navController: NavHostController, viewModel: HomeViewModel) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.label_home_title2),
                style = MaterialTheme.typography.headlineSmall
            )

            TextButton(
                onClick = {

                    val route = Cocktails.route
                        .replace("{ids}", cocktails
                            .shuffled()
                            .take(30)
                            .joinToString(",") { it.id.toString() })
                        .replace("{top_bar_title}", "sheet_suggestions")
                    navController.navigate(route)
                },
                content = { Text(stringResource(R.string.label_home_title3)) }
            )
        }

        Spacer(Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(5.dp)
        ) {
            items(cocktails.take(10)) { cocktailItem ->

                Box(
                    Modifier.fillMaxWidth()
                ) {
                    CostumShadowBox(
                        elevation = 6.dp,
                        shadowPositions = setOf(ShadowPosition.TOP, ShadowPosition.LEFT),
                        cornerRadius = 12.dp,
                        shadowColor = MaterialTheme.colorScheme.secondary
                    ) {

                        Box {
                            CostumAsyncImage(
                                modifier = Modifier
                                    .width(250.dp)
                                    .height(250.dp)
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

                            IconButton(
                                onClick = {
                                    viewModel.updateFavorite(cocktailItem)
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.updateFavorite(cocktailItem)},
                                    content = {
                                        if (cocktailItem.favorited)
                                            Icon(painterResource(R.drawable.ic_favorite_on), "Favorite On")
                                        else
                                            Icon(painterResource(R.drawable.ic_favorite_off), "Favorite Off")
                                    }
                                )
                            }
                        }
                    }

                    TextWithShadow(
                        text = cocktailItem.name,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

