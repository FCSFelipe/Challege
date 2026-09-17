package br.com.fiap.myapplication.screens.employee

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import br.com.fiap.myapplication.R

@Composable
fun OperatorDashboard(
    navController: NavController
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var nomeDoUsuario = currentUser?.displayName

    if (nomeDoUsuario.isNullOrEmpty()) {
        val email = currentUser?.email ?: ""
        nomeDoUsuario = email.substringBefore("@")
            .split(".")
            .firstOrNull()
            ?.replaceFirstChar { it.uppercase() }
            ?: "Operador"
    } else {
        nomeDoUsuario = nomeDoUsuario.split(" ").firstOrNull() ?: "Operador"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Olá, $nomeDoUsuario!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        DashboardActionItem(
            iconResId = R.drawable.horse_chess,
            title = "Estratégias",
            subtitle = "Ver direcionamentos",
            onClick = { navController.navigate("manageStrategy") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DashboardActionItem(
            iconResId = R.drawable.light_bulb,
            title = "Minhas Ideias",
            subtitle = "Ver status",
            onClick = { navController.navigate("ideaList") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DashboardActionItem(
            iconResId = R.drawable.papel_dobrado,
            title = "Nova Ideia",
            subtitle = "Registrar problema",
            onClick = { navController.navigate("addIdea") }
        )
    }
}

@Composable
fun DashboardActionItem(
    @DrawableRes iconResId: Int,
    title: String,
    subtitle: String,
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