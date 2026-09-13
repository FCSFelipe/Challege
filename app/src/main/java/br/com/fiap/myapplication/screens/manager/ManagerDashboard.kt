package br.com.fiap.myapplication.screens.manager

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun ManagerDashboard(
    navController: NavController
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var nomeDoGestor = currentUser?.displayName

    if (nomeDoGestor.isNullOrEmpty()) {
        val email = currentUser?.email ?: ""
        nomeDoGestor = email.substringBefore("@")
            .split(".")
            .firstOrNull()
            ?.replaceFirstChar { it.uppercase() }
            ?: "Gestor"
    } else {
        nomeDoGestor = nomeDoGestor.split(" ").firstOrNull() ?: "Gestor"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Olá, $nomeDoGestor!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(48.dp))

        ManagerActionItem(
            iconResId = R.drawable.horse_chess,
            title = "Estratégias",
            subtitle = "Ver direcionamentos",
            onClick = {
                navController.navigate("manageStrategy")
            }
        )

        ManagerActionItem(
            iconResId = R.drawable.light_bulb,
            title = "Ideias Pendentes",
            subtitle = "Ver status",
            onClick = {
                navController.navigate("ideaList")
            }
        )

        ManagerActionItem(
            iconResId = R.drawable.papel_dobrado,
            title = "Projetos",
            subtitle = "Registrar problema",
            onClick = {
                navController.navigate("manageProjects")
            }
        )
    }
}

@Composable
fun ManagerActionItem(
    @DrawableRes iconResId: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFF5F6F8), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3142),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        HorizontalDivider(
            color = Color(0xFFEBEBEB),
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}