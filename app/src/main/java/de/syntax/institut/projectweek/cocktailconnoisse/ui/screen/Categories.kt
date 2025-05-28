package de.syntax.institut.projectweek.cocktailconnoisse.ui.screen

import android.R.attr.bottom
import android.R.attr.contentDescription
import android.R.attr.shadowColor
import android.R.attr.text
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImagePainter.State.Empty.painter
import de.schinke.steffen.enums.ShadowPosition
import de.schinke.steffen.enums.SnackbarDisplayTime
import de.schinke.steffen.enums.SnackbarMode
import de.schinke.steffen.enums.ViewModelState
import de.schinke.steffen.extensions.sendMessageOnSnackbar
import de.schinke.steffen.interfaces.AppRouteContent
import de.schinke.steffen.interfaces.AppRouteSheet
import de.schinke.steffen.interfaces.AppRouteTab
import de.schinke.steffen.ui.components.CostumErrorImage
import de.schinke.steffen.ui.components.CostumProgressCircle
import de.schinke.steffen.ui.components.CostumShadowBox
import de.syntax.institut.projectweek.cocktailconnoisse.R
import de.syntax.institut.projectweek.cocktailconnoisse.data.model.Category
import de.syntax.institut.projectweek.cocktailconnoisse.extension.getStringResourceByName
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.CostumTopBarBackground
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.TextWithShadow
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
                val apiError by viewModel.apiError.collectAsState()
                val categories by viewModel.categories.collectAsState()

                when (viewModelState) {

                    ViewModelState.READY -> {
                        Content(navController, categories)
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
    private fun Content(navController: NavHostController, categories: List<Category>) {
        Column(Modifier.fillMaxSize()) {

            Log.d("Categories", "categories: $categories")

            LazyColumn {
                item {
                    OneItem(
                        category = categories[0],
                        navController = navController
                    )
                }
                item {
                    TwoItems(
                        categoryOne = categories[1],
                        categoryTwo = categories[2],
                        navController = navController
                    )
                }
                item {
                    OneItem(
                        category = categories[3],
                        navController = navController
                    )
                }
                item {
                    TwoItems(
                        categoryOne = categories[4],
                        categoryTwo = categories[5],
                        navController = navController
                    )
                }
                item {
                    OneItem(
                        category = categories[6],
                        navController = navController
                    )
                }
                item {
                    TwoItems(
                        categoryOne = categories[7],
                        categoryTwo = categories[8],
                        navController = navController
                    )
                }
                item {
                    OneItem(
                        category = categories[9],
                        navController = navController
                    )
                }
                item {
                    OneItem(
                        category = categories[10],
                        navController = navController
                    )
                }
            }
        }
    }


    @Composable
    private fun OneItem(category: Category, navController: NavHostController) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp)
                .padding(bottom = 30.dp, top = 10.dp)
        ) {
            CostumShadowBox(
                elevation = 6.dp,
                shadowPositions = setOf(ShadowPosition.TOP, ShadowPosition.LEFT),
                cornerRadius = 20.dp,
                shadowColor = MaterialTheme.colorScheme.secondary
            ) {
                Image(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .fillMaxWidth()
                        .clickable(onClick = {
                            /*navController.navigate(
                            Details.route.replace(
                                "{id}",
                                category
                            )

                        )*/
                        }),
                    painter = painterResource(id = category.imageId),
                    contentDescription = category.name
                )
            }
            TextWithShadow(
                text = category.name,
                fontSize = 16.sp
            )
        }
    }

    @Composable
    fun TwoItems(categoryOne: Category, categoryTwo: Category, navController: NavHostController) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp))
        {
            Box(Modifier
                .padding(start = 6.dp)
            ) {
                CostumShadowBox(
                    Modifier
                        .height(180.dp)
                        .width(180.dp),
                        elevation = 6.dp,
                    shadowPositions = setOf(ShadowPosition.TOP, ShadowPosition.LEFT),
                    cornerRadius = 20.dp,
                    shadowColor = MaterialTheme.colorScheme.secondary
                ) {
                    Image(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(onClick = {
                                /* navController.navigate(
                                     Details.route.replace(
                                         "{id}",
                                         category
                                     )

                                )*/
                            }),
                        painter = painterResource(id = categoryOne.imageId),
                        contentDescription = categoryOne.name
                    )
                }
                TextWithShadow(
                    text = categoryOne.name,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(40.dp))

            Box(
                Modifier
                    .padding(start = 6.dp)
            ) {
                CostumShadowBox(
                    Modifier
                        .height(180.dp)
                        .width(180.dp),
                    elevation = 6.dp,
                    shadowPositions = setOf(ShadowPosition.TOP, ShadowPosition.LEFT),
                    cornerRadius = 20.dp,
                    shadowColor = MaterialTheme.colorScheme.secondary
                ) {
                    Image(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(onClick = {
                                /* navController.navigate(
                                     Details.route.replace(
                                         "{id}",
                                         category
                                     )

                                )*/
                            }),

                        painter = painterResource(id = categoryTwo.imageId),
                        contentDescription = categoryTwo.name
                    )
                }
                TextWithShadow(
                    text = categoryTwo.name,
                    fontSize = 16.sp
                )
            }
        }
    }
}