package br.com.fiap.myapplication.screens.manager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.fiap.myapplication.R
import br.com.fiap.myapplication.components.BottomMenu
import br.com.fiap.myapplication.viewmodel.ManagerViewModel

@Composable
fun ProjectManagementScreen(
    navController: NavController,
    viewModel: ManagerViewModel = viewModel()
) {
    val projetos by viewModel.projetos
    val isLoading by viewModel.isLoading

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.buscarProjetos()
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
                    modifier = Modifier.height(24.dp)
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
        bottomBar = { BottomMenu(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo Projeto")
            }
        }
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
                text = "Gestão de Projetos",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading && projetos.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (projetos.isEmpty()) {
                Text("Nenhum projeto cadastrado.", color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(projetos) { projeto ->
                        ProjectListItem(
                            projeto = projeto,
                            onAtualizar = { id, progresso, resultados, lucro, investimento, prazo ->
                                viewModel.atualizarProjeto(id, progresso, resultados, lucro, investimento, prazo)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        if (showDialog) {
            var novoNome by remember { mutableStateOf("") }
            var novaDescricao by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Novo Projeto", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = novoNome, onValueChange = { novoNome = it }, label = { Text("Nome da Iniciativa") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = novaDescricao, onValueChange = { novaDescricao = it }, label = { Text("Descrição / Objetivo") }, modifier = Modifier.fillMaxWidth().height(100.dp))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (novoNome.isNotBlank()) {
                                viewModel.cadastrarProjeto(novoNome, novaDescricao)
                                showDialog = false
                            }
                        }
                    ) { Text("Salvar") }
                },
                dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar", color = Color.Gray) } }
            )
        }
    }
}

@Composable
fun ProjectListItem(
    projeto: Map<String, Any>,
    onAtualizar: (String, Float, String, String, String, String) -> Unit
) {
    val id = projeto["id"] as? String ?: ""
    val nome = projeto["nome"] as? String ?: "Projeto sem nome"
    val progressoAtual = (projeto["progresso"] as? Number)?.toFloat() ?: 0f

    val resultadosOriginais = projeto["resultados"] as? String ?: ""
    val lucroOriginal = projeto["lucro"] as? String ?: ""
    val investimentoOriginal = projeto["investimento"] as? String ?: ""
    val prazoOriginal = projeto["prazo"] as? String ?: ""

    var expanded by remember { mutableStateOf(false) }

    var sliderValue by remember(progressoAtual) { mutableFloatStateOf(progressoAtual) }
    var resultText by remember(resultadosOriginais) { mutableStateOf(resultadosOriginais) }
    var lucroText by remember(lucroOriginal) { mutableStateOf(lucroOriginal) }
    var investText by remember(investimentoOriginal) { mutableStateOf(investimentoOriginal) }
    var prazoText by remember(prazoOriginal) { mutableStateOf(prazoOriginal) }

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
                text = nome,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { progressoAtual / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFF0F0F0),
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "${progressoAtual.toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (expanded) "Cancelar atualização" else "Acompanhar e Atualizar",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { expanded = !expanded }
            )

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text("Andamento Físico", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Slider(value = sliderValue, onValueChange = { sliderValue = it }, valueRange = 0f..100f, steps = 10)
                    Text("Marcando: ${sliderValue.toInt()}%", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Dados Financeiros para o Dashboard", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = lucroText,
                        onValueChange = { lucroText = it },
                        label = { Text("Lucro (Apenas números)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = investText,
                            onValueChange = { investText = it },
                            label = { Text("Investimento") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = prazoText,
                            onValueChange = { prazoText = it },
                            label = { Text("Prazo") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = resultText,
                        onValueChange = { resultText = it },
                        label = { Text("Resumo de Resultados Obtidos") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onAtualizar(id, sliderValue, resultText, lucroText, investText, prazoText)
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Salvar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}