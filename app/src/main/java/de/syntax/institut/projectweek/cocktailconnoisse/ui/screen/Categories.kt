package de.syntax.institut.projectweek.cocktailconnoisse.ui.screen

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import de.syntax.institut.projectweek.cocktailconnoisse.extension.getStringResourceByName
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.CategoryRow
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.CategoryRowTwice
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.CostumTopBarBackground
import de.syntax.institut.projectweek.cocktailconnoisse.ui.sheet.Filters
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.CategoriesViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.KClass

object Categories : AppRouteTab, AppRouteContent {

    override val tabTitle: String
        @Composable
        get() = stringResource(R.string.screen_categories)

    override val tabIcon: @Composable (() -> Unit)
        get() = { Icon(Icons.Default.Category, null) }

    override val route: String
        get() = "categories"

    override val viewModelDependencies: Map<KClass<out ViewModel>, @Composable (() -> ViewModel)>
        get() = mapOf(
            CategoriesViewModel::class to { koinViewModel<CategoriesViewModel>() }
        )

    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, SheetState, Bundle?, (AppRouteSheet, Bundle?) -> Unit, () -> Unit) -> Unit)?
        get() = { viewModelMap, navController, _, _, _, _ ->

            val viewModel =
                viewModelMap.getOrDefault(CategoriesViewModel::class, null) as CategoriesViewModel?
            viewModel?.let { viewModel ->

                val viewModelState by viewModel.state.collectAsState()
                val apiError by viewModel.repoError.collectAsState()
                val categories by viewModel.categories.collectAsState()
                val listForNavigationIds by viewModel.listForNavigationIds.collectAsState()

                LaunchedEffect(listForNavigationIds) {
                    if (listForNavigationIds != null && !viewModel.hasNavigation.value) {

                        Log.d("Categories", "listForNavigationIds: $listForNavigationIds")

                        viewModel.setNavigation()

                        val route = Cocktails.route
                            .replace("{ids}", listForNavigationIds!!)
                            .replace("{top_bar_title}",
                                viewModel.clickedCategory.value?.name ?: "Cocktails"
                            )

                        navController.navigate(route)

                        viewModel.resetNavigation()
                    }
                }

                when (viewModelState) {

                    ViewModelState.READY -> {
                        Content(viewModel, categories)
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
        get() = { _, _, onShowSheet ->

            Box {
                CostumTopBarBackground()
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.screen_categories),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    actions = {
                        IconButton(
                            onClick = { onShowSheet(Filters, null) },
                            content = {
                                Icon(
                                    painterResource(de.schinke.steffen.ui.R.drawable.ic_filte),
                                    null
                                )
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                )
            }
        }

    override val fab: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, (AppRouteSheet, Bundle?) -> Unit) -> Unit)?
        get() = null


    @Composable
    private fun Content(viewModel: CategoriesViewModel, categories: List<Category>) {

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            val chunked = categories.chunked(3)

            items(chunked) { chunk ->
                when (chunk.size) {
                    1 -> {
                        CategoryRow(chunk[0]) {
                            viewModel.onCategoryClick(it)
                        }
                    }
                    2 -> {
                        CategoryRowTwice(chunk[0], chunk[1]) {
                            viewModel.onCategoryClick(it)
                        }
                    }
                    3 -> {
                        CategoryRow(chunk[0]) {
                            viewModel.onCategoryClick(it)
                        }
                        CategoryRowTwice(chunk[0], chunk[1]) {
                            viewModel.onCategoryClick(it)
                        }
                    }
                }
            }
        }
    }
}