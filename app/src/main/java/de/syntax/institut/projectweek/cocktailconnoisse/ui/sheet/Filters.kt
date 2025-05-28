package de.syntax.institut.projectweek.cocktailconnoisse.ui.sheet

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import de.schinke.steffen.interfaces.AppRoute
import de.schinke.steffen.interfaces.AppRouteSheet
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.FilterSheet
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.CategoriesViewModel
import kotlin.reflect.KClass

object Filters : AppRoute, AppRouteSheet {
    override val route: String
        get() = "filters"

    override val viewModelDependencies: Map<KClass<out ViewModel>, @Composable (() -> ViewModel)>
        get() = mapOf()

    @OptIn(ExperimentalMaterial3Api::class)
    override val contentSheet: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, SheetState, Bundle?, (AppRouteSheet, Bundle?) -> Unit, () -> Unit) -> Unit)
        get() = { viewModels, _, _, _, _, _ ->

            val viewModel = viewModels[CategoriesViewModel::class] as CategoriesViewModel

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
            ) {

                FilterSheet(viewModel = viewModel, ingredient = "Vodka")
            }
        }
}

