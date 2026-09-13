package br.com.fiap.myapplication.screens.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.fiap.myapplication.components.BottomMenu
import br.com.fiap.myapplication.viewmodel.LeadershipViewModel

@Composable
fun ExecutiveDashboardScreen(
    navController: NavController,
    viewModel: LeadershipViewModel = viewModel()
) {
    val roiReal by viewModel.roi
    val lucroReal by viewModel.lucro
    val quantidadeProjetos by viewModel.quantidadeProjetosAtivos

    val projetos by viewModel.projetos

    Scaffold(
        bottomBar = { BottomMenu(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Dashboard Executivo",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A2E5A)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F6F8)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Resultados Gerais", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2D3142))
                    Spacer(modifier = Modifier.height(16.dp))

                    MetricRow("ROI Global", roiReal)
                    MetricRow("Lucro Acumulado", lucroReal)
                    MetricRow("Projetos Ativos", quantidadeProjetos.toString())
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Desempenho por Projeto",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A2E5A),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (projetos.isEmpty()) {
                Text("Nenhum projeto ativo no momento.", color = Color.Gray, modifier = Modifier.padding(bottom = 32.dp))
            } else {
                projetos.forEach { projeto ->
                    ProjectPerformanceCard(projeto)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text(text = value, color = Color(0xFF2E4D8F), fontWeight = FontWeight.Bold, fontSize = 17.sp)
    }
}

@Composable
fun ProjectPerformanceCard(projeto: Map<String, Any>) {
    val nome = projeto["nome"] as? String ?: "Projeto"
    val roiProj = projeto["roi"] as? String ?: "N/A"
    val lucroProj = projeto["lucro"] as? String ?: "N/A"
    val investimento = projeto["investimento"] as? String ?: "N/A"
    val prazo = projeto["prazo"] as? String ?: "N/A"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEBEBEB))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("ROI", fontSize = 12.sp, color = Color.Gray)
                    Text(roiProj, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3142))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Lucro", fontSize = 12.sp, color = Color.Gray)
                    Text(lucroProj, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3142))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Investimento", fontSize = 12.sp, color = Color.Gray)
                    Text(investimento, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3142))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Prazo", fontSize = 12.sp, color = Color.Gray)
                    Text(prazo, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3142))
                }
            }
        }
    }
}