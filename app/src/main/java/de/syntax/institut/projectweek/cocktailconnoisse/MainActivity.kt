package de.syntax.institut.projectweek.cocktailconnoisse

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import de.schinke.steffen.ui.helpers.AppLauncher
import de.schinke.steffen.ui.helpers.AppNavigator
import de.syntax.institut.projectweek.cocktailconnoisse.ui.composable.Launch
import de.syntax.institut.projectweek.cocktailconnoisse.ui.screen.*
import de.syntax.institut.projectweek.cocktailconnoisse.ui.sheet.Filters
import de.syntax.institut.projectweek.cocktailconnoisse.ui.theme.CocktailConnoisseTheme
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.seconds

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app.cocktails.settings")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: SettingsViewModel = koinViewModel()
            val isDarkTheme = viewModel.isDarkTheme.collectAsState().value

            // StatusBar anpassen je nach Theme
            SideEffect {
                enableEdgeToEdge(
                    statusBarStyle = if (!isDarkTheme) {
                        SystemBarStyle.dark(Color.Transparent.toArgb())
                    } else {
                        SystemBarStyle.light(
                            Color.White.toArgb(),
                            Color.Black.toArgb()
                        )
                    }
                )
            }

            CocktailConnoisseTheme(darkTheme = isDarkTheme) {
                val navigationBottomBarColor = MaterialTheme.colorScheme.secondary

                val navigationBottomBarItemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.secondary,
                    unselectedIconColor = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.onSecondaryContainer,
                    unselectedTextColor = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
                AppLauncher(
                    duration = 3.seconds,
                    launchContent = { Launch() }
                ) {
                    AppNavigator(
                        modifier = Modifier.padding(top = 70.dp),
                        startScreen = Home,
                        allRoutes = listOf(
                            Home, Favorites, Categories, Settings, Details, Filters, Cocktails
                        ),
                        allTabRoutes = listOf(
                            Home, Favorites, Categories, Settings
                        ),
                        navigationBottomBarColor = navigationBottomBarColor,
                        navgationBottomBarItemColors = navigationBottomBarItemColors
                    )
                }
            }
        }
    }
}
