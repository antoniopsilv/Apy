package br.edu.ifsp.apy.view.ui
import br.edu.ifsp.apy.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.places.api.Places


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o Places API aqui
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "presentation_screen"
                ) {
                    composable("presentation_screen") {
                        HomeScreen(navController)
                    }
                    composable("history_screen") {
                        HistoryScreen(navController)
                    }
                    composable("settings_screen") {
                        SettingsScreen(navController)
                    }
                }
            }
        }

    }
}

