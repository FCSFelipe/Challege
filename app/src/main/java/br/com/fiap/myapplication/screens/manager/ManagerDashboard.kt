package br.com.fiap.myapplication.screens.manager

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.fiap.myapplication.R
import br.com.fiap.myapplication.viewmodel.HomeViewModel

@Composable
fun ManagerDashboard(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val userName by homeViewModel.userName

    ManagerDashboardContent(
        userName = userName,
        onNavigate = { rota -> navController.navigate(rota) }
    )
}

@Composable
fun ManagerDashboardContent(
    userName: String,
    onNavigate: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Olá, $userName!",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            DashboardActionCard(
                title = "Estratégias",
                subtitle = "Ver direcionamentos",
                iconResId = R.drawable.horse_chess,
                onClick = { onNavigate("manageStrategy") }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            DashboardActionCard(
                title = "Ideias Pendentes",
                subtitle = "Ver status",
                iconResId = R.drawable.light_bulb,
                onClick = { onNavigate("ideaList") }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            DashboardActionCard(
                title = "Projetos",
                subtitle = "Registrar problema",
                iconResId = R.drawable.papel_dobrado,
                onClick = { onNavigate("manageProjects") }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DashboardActionCard(
    title: String,
    subtitle: String,
    @DrawableRes iconResId: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(24.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3142),
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}