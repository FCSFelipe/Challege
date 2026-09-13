package br.com.fiap.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.fiap.myapplication.screens.AddIdeaScreen
import br.com.fiap.myapplication.screens.HomeScreen
import br.com.fiap.myapplication.screens.LoginScreen
import br.com.fiap.myapplication.screens.SignupScreen
import br.com.fiap.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(dynamicColor = false) {
                AppNavigation()
            }
        }
    }
}

