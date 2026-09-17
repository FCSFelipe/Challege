package br.com.fiap.myapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.fiap.myapplication.R
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
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logotipo),
                    contentDescription = null,
                    modifier = Modifier.height(36.dp)
                )
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            }
        },
        bottomBar = { BottomMenu(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (temPermissaoGestao) "Gestão de Ideias" else "Minhas Ideias",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Buscar", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F6F8),
                    unfocusedContainerColor = Color(0xFFF5F6F8),
                    disabledContainerColor = Color(0xFFF5F6F8),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (ideiasFiltradas.isEmpty()) {
                Text("Nenhuma ideia encontrada.", color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
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

    val statusUpper = status.uppercase()
    val (corFundoStatus, corTextoStatus) = when (statusUpper) {
        "APROVADA" -> Pair(Color(0xFFE8F5E9), Color(0xFF388E3C))
        "REPROVADA" -> Pair(Color(0xFFFFEBEE), Color(0xFFD32F2F))
        else -> Pair(Color(0xFFFFF3E0), Color(0xFFF57C00))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Categoria : $categoria", fontSize = 14.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = prioridade, fontSize = 14.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Status: ", fontSize = 14.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(corFundoStatus, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = status,
                        color = corTextoStatus,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (temPermissaoGestao) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Definir Prioridade:",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .border(1.dp, Color(0xFFEBEBEB), RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    PrioritySegment("Baixa", prioridade == "Baixa", Modifier.weight(1f)) { onAtualizarPrioridade(id, "Baixa") }
                    Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFFEBEBEB)))
                    PrioritySegment("Média", prioridade == "Média", Modifier.weight(1f)) { onAtualizarPrioridade(id, "Média") }
                    Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFFEBEBEB)))
                    PrioritySegment("Alta", prioridade == "Alta", Modifier.weight(1f)) { onAtualizarPrioridade(id, "Alta") }
                }

                if (statusUpper != "APROVADA" && statusUpper != "REPROVADA") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onAtualizarStatus(id, "Aprovada") },
                            modifier = Modifier.weight(1f).height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Aprovar", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onAtualizarStatus(id, "Reprovada") },
                            modifier = Modifier.weight(1f).height(44.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF44336)),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF44336))
                        ) {
                            Text("Reprovar", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrioritySegment(
    text: String,
    isSelected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.DarkGray
        )
    }
}