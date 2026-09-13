package br.com.fiap.myapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.fiap.myapplication.components.BottomMenu
import br.com.fiap.myapplication.screens.employee.OperatorDashboard
import br.com.fiap.myapplication.screens.leadership.LeadershipDashboard
import br.com.fiap.myapplication.screens.manager.ManagerDashboard
import br.com.fiap.myapplication.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val userRole by homeViewModel.userRole
    val isLoading by homeViewModel.isLoading

    Scaffold(
        bottomBar = {
            BottomMenu(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (userRole) {
                    "OPERADOR" -> OperatorDashboard(navController = navController)
                    "GESTOR" -> ManagerDashboard(navController = navController)
                    "LIDERANCA" -> LeadershipDashboard(navController = navController)
                    else -> OperatorDashboard(navController = navController)
                }
            }
        }
    }
}