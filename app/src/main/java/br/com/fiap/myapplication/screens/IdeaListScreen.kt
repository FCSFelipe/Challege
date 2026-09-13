package br.com.fiap.myapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import br.com.fiap.myapplication.viewmodel.HomeViewModel
import br.com.fiap.myapplication.viewmodel.ManagerViewModel
import br.com.fiap.myapplication.viewmodel.OperatorViewModel

@Composable
fun IdeaListScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(),
    operatorViewModel: OperatorViewModel = viewModel(),
    managerViewModel: ManagerViewModel = viewModel()
) {
    val userRoleRaw by homeViewModel.userRole
    val userRole = userRoleRaw.uppercase()

    val temPermissaoGestao = userRole.contains("GESTOR") || userRole.contains("LIDER")

    var searchText by remember { mutableStateOf("") }

    val ideiasParaExibir = if (temPermissaoGestao) managerViewModel.todasIdeias.value else operatorViewModel.minhasIdeias.value
    val isLoading = if (temPermissaoGestao) managerViewModel.isLoading.value else operatorViewModel.isLoading.value

    LaunchedEffect(userRole) {
        if (temPermissaoGestao) {
            managerViewModel.buscarTodasIdeias()
        }
    }

    val ideiasFiltradas = ideiasParaExibir.filter {
        it["titulo"].toString().contains(searchText, ignoreCase = true)
    }

    Scaffold(
        bottomBar = { BottomMenu(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (temPermissaoGestao) "Gestão de Ideias" else "Minhas Ideias",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Buscar", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F6F8),
                    unfocusedContainerColor = Color(0xFFF5F6F8),
                    disabledContainerColor = Color(0xFFF5F6F8),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.LightGray) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else if (ideiasFiltradas.isEmpty()) {
                Text("Nenhuma ideia encontrada.", color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(ideiasFiltradas) { ideia ->
                        IdeaListItem(
                            ideia = ideia,
                            temPermissaoGestao = temPermissaoGestao,
                            onAtualizarStatus = { id, status ->
                                if (temPermissaoGestao) managerViewModel.atualizarStatusIdeia(id, status)
                            },
                            onAtualizarPrioridade = { id, prioridade ->
                                if (temPermissaoGestao) managerViewModel.atualizarPrioridadeIdeia(id, prioridade)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IdeaListItem(
    ideia: Map<String, Any>,
    temPermissaoGestao: Boolean,
    onAtualizarStatus: (String, String) -> Unit,
    onAtualizarPrioridade: (String, String) -> Unit
) {
    val id = ideia["id"] as? String ?: ""
    val titulo = ideia["titulo"] as? String ?: "Sem título"
    val status = ideia["status"] as? String ?: "Em análise"
    val categoria = ideia["categoria"] as? String ?: "Geral"
    val prioridade = ideia["prioridade"] as? String ?: "Não definida"

    val corStatus = when (status.uppercase()) {
        "APROVADA" -> Color(0xFF4CAF50)
        "REPROVADA" -> Color(0xFFF44336)
        else -> Color(0xFFFFA000)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.size(48.dp).background(Color(0xFFF5F6F8), CircleShape))
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))

                IdeaDetailRow("Categoria:", categoria)
                IdeaDetailRow("Prioridade:", prioridade)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("• ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Status: ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = status, fontSize = 14.sp, color = corStatus, fontWeight = FontWeight.Bold)
                }

                if (temPermissaoGestao) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Definir Prioridade:", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PriorityButton("Baixa", prioridade) { onAtualizarPrioridade(id, "Baixa") }
                        PriorityButton("Média", prioridade) { onAtualizarPrioridade(id, "Média") }
                        PriorityButton("Alta", prioridade) { onAtualizarPrioridade(id, "Alta") }
                    }

                    if (status.uppercase() != "APROVADA" && status.uppercase() != "REPROVADA") {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onAtualizarStatus(id, "Aprovada") },
                                modifier = Modifier.weight(1f).height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("Aprovar", fontSize = 12.sp, fontWeight = FontWeight.Bold) }

                            OutlinedButton(
                                onClick = { onAtualizarStatus(id, "Reprovada") },
                                modifier = Modifier.weight(1f).height(40.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF44336)),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("Reprovar", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)
    }
}

@Composable
fun IdeaDetailRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("• ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = "$label ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = value, fontSize = 14.sp, color = Color.DarkGray)
    }
}

@Composable
fun PriorityButton(text: String, prioridadeAtual: String, onClick: () -> Unit) {
    val isSelected = text == prioridadeAtual
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF5F6F8)
    val contentColor = if (isSelected) Color.White else Color.Gray

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(32.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}