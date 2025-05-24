package de.syntax.institut.projectweek.cocktailconnoisse

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import de.schinke.steffen.ui.components.DefaultLaunch
import de.schinke.steffen.ui.helpers.AppLauncher
import de.schinke.steffen.ui.helpers.AppNavigator
import de.syntax.institut.projectweek.cocktailconnoisse.ui.screen.Categories
import de.syntax.institut.projectweek.cocktailconnoisse.ui.screen.Cocktails
import de.syntax.institut.projectweek.cocktailconnoisse.ui.screen.Details
import de.syntax.institut.projectweek.cocktailconnoisse.ui.screen.Favorites
import de.syntax.institut.projectweek.cocktailconnoisse.ui.screen.Settings
import de.syntax.institut.projectweek.cocktailconnoisse.ui.sheet.Filters
import de.syntax.institut.projectweek.cocktailconnoisse.ui.theme.CocktailConnoisseTheme
import de.syntax.institut.projectweek.cocktailconnoisse.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.seconds

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app.cocktails")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val viewModel: SettingsViewModel = koinViewModel()
            val isDarkTheme = viewModel.isDarkMode.collectAsState().value

            // status panel customization
            enableEdgeToEdge(
                statusBarStyle = if (isDarkTheme) {
                    SystemBarStyle.dark(
                        Color.Transparent.toArgb())
                } else {
                    SystemBarStyle.light(
                        Color.Transparent.toArgb(),
                        Color.Transparent.toArgb())
                }
            )

            CocktailConnoisseTheme(darkTheme = isDarkTheme) {

                AppLauncher(

                    duration = 3.seconds,
                    launchContent = {

                        // TODO sts 23.05.25 - hier composable start screen mit oder ohne anime
                        DefaultLaunch()
                    }
                ) {

                    AppNavigator(
                        startScreen = Cocktails,
                        allRoutes =
                            listOf(
                                Cocktails,
                                Favorites,
                                Categories,
                                Settings,
                                Details,
                                Filters
                            ),    // bei weiteren screens od sheets muss hier eingefügt werden
                        allTabRoutes =
                            listOf(
                                Cocktails,
                                Favorites,
                                Categories,
                                Settings
                            )  // bei weiteren tabs muss hier eingefügt werden
                    )
                }
            }
        }
    }
}
