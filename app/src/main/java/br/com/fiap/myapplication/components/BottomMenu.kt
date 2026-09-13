package br.com.fiap.myapplication.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.fiap.myapplication.ui.theme.MyApplicationTheme

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    object Home : BottomNavItem("Home", Icons.Outlined.Home, "home")
    object Ideias : BottomNavItem("Ideias", Icons.Outlined.Lightbulb, "ideaList")
    object Perfil : BottomNavItem("Perfil", Icons.Outlined.Person, "perfil")
}

@Composable
fun BottomMenu(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Ideias,
        BottomNavItem.Perfil
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBar(
            modifier = Modifier.navigationBarsPadding(),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                    label = { Text(text = item.title) },
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) { saveState = true }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomMenuPreview() {
    MyApplicationTheme {
        BottomMenu(rememberNavController())
    }
}