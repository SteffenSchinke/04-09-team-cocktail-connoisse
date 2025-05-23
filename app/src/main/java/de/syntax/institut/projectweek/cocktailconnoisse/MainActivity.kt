package de.syntax.institut.projectweek.cocktailconnoisse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.schinke.steffen.ui.components.DefaultLaunch
import de.schinke.steffen.ui.helpers.AppLauncher
import de.schinke.steffen.ui.helpers.AppNavigator
import de.syntax.institut.projectweek.cocktailconnoisse.ui.screen.Cocktails
import de.syntax.institut.projectweek.cocktailconnoisse.ui.screen.Settings
import de.syntax.institut.projectweek.cocktailconnoisse.ui.theme.CocktailConnoisseTheme
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CocktailConnoisseTheme {

                AppLauncher(

                    duration = 3.seconds,
                    launchContent = {

                        // TODO sts 23.05.25 - hier composable start screen mit oder ohne anime
                        DefaultLaunch()
                    }
                ) {

                    AppNavigator(
                        startScreen = Cocktails,
                        allRoutes = listOf(Cocktails, Settings),    // bei weiteren screens od sheets muss hier eingefügt werden
                        allTabRoutes = listOf(Cocktails, Settings)  // bei weiteren tabs muss hier eingefügt werden
                    )
                }
            }
        }
    }
}
