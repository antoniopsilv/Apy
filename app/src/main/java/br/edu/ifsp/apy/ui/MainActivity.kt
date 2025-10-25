package br.edu.ifsp.apy.ui
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                        HistoryScreen()
                    }
                }
            }
        }

    }
}

