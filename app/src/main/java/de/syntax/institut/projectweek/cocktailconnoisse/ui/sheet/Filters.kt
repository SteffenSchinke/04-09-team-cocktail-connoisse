package de.syntax.institut.projectweek.cocktailconnoisse.ui.sheet

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import de.schinke.steffen.interfaces.AppRoute
import de.schinke.steffen.interfaces.AppRouteSheet
import de.syntax.institut.projectweek.cocktailconnoisse.R
import kotlin.reflect.KClass

object Filters : AppRoute, AppRouteSheet {
    override val route: String
        get() = "filters"

    override val viewModelDependencies: Map<KClass<out ViewModel>, @Composable (() -> ViewModel)>
        get() = mapOf()

    @OptIn(ExperimentalMaterial3Api::class)
    override val contentSheet: @Composable ((
        Map<KClass<out ViewModel>, ViewModel>, NavHostController,
        SheetState, Bundle?, (AppRouteSheet, Bundle?) -> Unit, () -> Unit
    ) -> Unit)
        get() = { _, _, _, _, _, _ ->

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(R.string.sheet_filters),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }


}