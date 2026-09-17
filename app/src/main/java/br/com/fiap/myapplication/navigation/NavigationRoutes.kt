package br.com.fiap.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.fiap.myapplication.screens.AddIdeaScreen
import br.com.fiap.myapplication.screens.AddProjectScreen
import br.com.fiap.myapplication.screens.manager.ExecutiveDashboardScreen
import br.com.fiap.myapplication.screens.HomeScreen
import br.com.fiap.myapplication.screens.LoginScreen
import br.com.fiap.myapplication.screens.manager.ManageStrategyScreen
import br.com.fiap.myapplication.screens.SignupScreen
import java.net.URLDecoder
import br.com.fiap.myapplication.screens.IdeaListScreen
import br.com.fiap.myapplication.screens.ProfileScreen
import br.com.fiap.myapplication.screens.manager.ProjectManagementScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") { LoginScreen(navController = navController) }

        composable("signup") { SignupScreen(navController = navController) }

        composable("home") { HomeScreen(navController = navController) }

        composable("addIdea") { AddIdeaScreen(navController = navController) }

        composable("ideaList") { IdeaListScreen(navController = navController) }

        composable("manageStrategy") { ManageStrategyScreen(navController = navController) }

        composable("manageProjects") { ProjectManagementScreen(navController = navController) }

        composable("perfil") { ProfileScreen(navController = navController) }

        composable("executiveDashboard") {
            ExecutiveDashboardScreen(navController = navController) }

        composable("addProject/{ideaId}/{ideaTitle}") { backStackEntry ->
            val ideaId = backStackEntry.arguments?.getString("ideaId") ?: ""
            val tituloCru = backStackEntry.arguments?.getString("ideaTitle") ?: ""
            val ideaTitle = URLDecoder.decode(tituloCru, "UTF-8")

            AddProjectScreen(
                navController = navController,
                ideaId = ideaId,
                ideaTitle = ideaTitle
            )
        }
    }
}