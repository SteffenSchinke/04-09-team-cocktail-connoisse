package de.syntax.institut.projectweek.cocktailconnoisse.ui.screen

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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


            val viewModelHome = viewModelMap[HomeViewModel::class] as HomeViewModel
            val viewModelSettings = viewModelMap[SettingsViewModel::class] as SettingsViewModel
            val viewModelState by viewModelHome.state.collectAsState()
            val apiError by viewModelHome.apiError.collectAsState()
            val randomCocktail by viewModelHome.randomCocktail.collectAsState()
            val randomCocktails by viewModelHome.randomCocktails.collectAsState()

            LaunchedEffect(Unit) {

                if (viewModelState == ViewModelState.READY) {

                    viewModelHome.loadCocktails()
                }
            }

            when (viewModelState) {

                ViewModelState.READY -> {
                    Content(viewModelHome, viewModelSettings, navController, randomCocktail, randomCocktails)
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


    @OptIn(ExperimentalMaterial3Api::class)
    override val topBar: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, (AppRouteSheet, Bundle?) -> Unit) -> Unit)?
        get() = { viewModelMap, _, _ ->

            val viewModel =
                viewModelMap.getOrDefault(HomeViewModel::class, null) as HomeViewModel?
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
    viewModelHome: HomeViewModel,
    viewModelSettings: SettingsViewModel,
    navController: NavHostController,
    cocktail: Cocktail?,
    cocktails: List<Cocktail>
) {
    var showALL by remember { mutableStateOf(false) }
    var isDarkMode = viewModelSettings.isDarkMode.collectAsState()

    if (!showALL) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = stringResource(R.string.lable_cocktail_title1),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Box(
                        Modifier.fillMaxWidth()
                    ) {
                        CostumAsyncImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clickable {
                                    navController.navigate(
                                        Details.route.replace("{id}", cocktail?.id.toString())
                                    )
                                },
                            url = cocktail?.imageUrl ?: ""
                        )
                        if (cocktail != null) {
                            cocktail.name?.let {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .padding(vertical = 20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = it,
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleLarge,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .widthIn(max = 200.dp)
                                            .padding(horizontal = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.lable_cocktail_title2),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    TextButton(onClick = { showALL = true }) {
                        Text("Alle anzeigen")
                    }
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cocktails.take(10)) { cocktailItem ->
                        Box(
                            Modifier.fillMaxWidth()
                        ) {
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
                                url = cocktailItem.imageUrl ?: ""
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(vertical = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = cocktailItem.name ?: "",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .widthIn(max = 200.dp)
                                        .padding(horizontal = 8.dp)
                                )


                            }
                        }
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Alle Cocktails",
                    style = MaterialTheme.typography.titleMedium
                )

                TextButton(onClick = { showALL = false }) {
                    Text("Zurück")
                }
            }
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
                            shadowPositions = setOf(ShadowPosition.TOP, ShadowPosition.LEFT),
                            cornerRadius = 12.dp,
                            shadowColor = if (isDarkMode.value) Color.White else Color.Black
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
                                url = cocktailItem.imageUrl ?: ""
                            )
                        }
                        Text(
                            text = cocktailItem.name ?: "",
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