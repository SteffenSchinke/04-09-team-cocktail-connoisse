package de.syntax.institut.projectweek.cocktailconnoisse.ui.screen

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import de.schinke.steffen.interfaces.AppRouteContent
import de.schinke.steffen.interfaces.AppRouteSheet
import de.schinke.steffen.interfaces.AppRouteTab
import de.syntax.institut.projectweek.cocktailconnoisse.R
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.CostumTopBarBackground
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.KClass

object Settings : AppRouteTab, AppRouteContent {

    override val tabTitle: String
        @Composable
        get() = stringResource(R.string.screen_settings)

    override val tabIcon: @Composable (() -> Unit)
        get() = { Icon(Icons.Default.Settings, null) }

    override val route: String
        get() = "settings"

    override val viewModelDependencies: Map<KClass<out ViewModel>, @Composable (() -> ViewModel)>
        get() = mapOf(
            SettingsViewModel::class to { koinViewModel<SettingsViewModel>() }
        )

    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable ((Map<KClass<out ViewModel>, ViewModel>, NavHostController, SheetState, Bundle?, (AppRouteSheet, Bundle?) -> Unit, () -> Unit) -> Unit)?
        get() = { viewModelMap, _, _, _, _, _ ->

            val viewModel =
                viewModelMap.getOrDefault(SettingsViewModel::class, null) as SettingsViewModel?
            viewModel?.let { viewModel ->

                val isDarkMode by viewModel.isDarkTheme.collectAsState()
                val isNotificationInfo by viewModel.isNotificationInfo.collectAsState()
                val isNotificationTip by viewModel.isNotificationTip.collectAsState()
                val isNotificationError by viewModel.isNotificationError.collectAsState()
                val isCacheEmpty by viewModel.isCacheEmpty.collectAsState()

                Column(Modifier.fillMaxSize()) {

                    Text(
                        stringResource(R.string.label_display),
                        modifier = Modifier.padding(bottom = 20.dp),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    SwitchRow(
                        isDarkMode,
                        stringResource(R.string.label_dark_mode_enabled),
                        stringResource(R.string.label_dark_mode_disabled),
                        viewModel::toggleIsDarkMode
                    )

                    Spacer(Modifier.height(20.dp))

                    Text(
                        stringResource(R.string.label_notifications),
                        modifier = Modifier.padding(bottom = 20.dp),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    SwitchRow(
                        isNotificationInfo,
                        stringResource(R.string.label_notification_info_on),
                        stringResource(R.string.label_notification_info_off),
                        viewModel::toggleIsNotificationInfo
                    )

                    SwitchRow(
                        isNotificationTip,
                        stringResource(R.string.label_notification_tip_on),
                        stringResource(R.string.label_notification_tip_off),
                        viewModel::toggleIsNotificationTip
                    )

                    SwitchRow(
                        isNotificationError,
                        stringResource(R.string.label_notification_error_on),
                        stringResource(R.string.label_notification_error_off),
                        viewModel::toggleIsNotificationError
                    )

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = stringResource(R.string.label_cache),
                        modifier = Modifier.padding(bottom = 20.dp),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = stringResource(R.string.label_cache_hit),
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(Modifier.weight(1f))

                        TextButton(
                            onClick = viewModel::deleteCache,
                            enabled = !isCacheEmpty,
                            content = {
                                Text(
                                    text = stringResource(R.string.label_cache_btn),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        ) 
                    }

                    Spacer(Modifier.height(20.dp))
                }
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override val topBar: @Composable ((
        Map<KClass<out ViewModel>, ViewModel>, NavHostController,
        (AppRouteSheet, Bundle?) -> Unit
    ) -> Unit)?
        get() = { _, _, _ ->

            Box {
                CostumTopBarBackground()
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.screen_settings),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
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

    @Composable
    private fun SwitchRow(

        switchValue: Boolean,
        switchLabelOn: String,
        switchLabelOff: String,
        onCheckedChange: (Boolean) -> Unit
    ) {

        Row(

            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text =
                    if (switchValue) switchLabelOn
                    else switchLabelOff,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.weight(1f))

            Switch(
                checked = switchValue,
                onCheckedChange = onCheckedChange
            )
        }
    }
}