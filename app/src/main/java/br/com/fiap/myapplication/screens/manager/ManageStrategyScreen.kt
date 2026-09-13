package br.com.fiap.myapplication.screens.manager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import br.com.fiap.myapplication.viewmodel.StrategyViewModel

@Composable
fun ManageStrategyScreen(
    navController: NavController,
    viewModel: StrategyViewModel = viewModel()
) {
    val estrategias by viewModel.estrategias
    val isLoading by viewModel.isLoading
    val podeEditar by viewModel.podeEditar

    var searchText by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var estrategiaEditando by remember { mutableStateOf<Map<String, Any>?>(null) }

    // 1. Variável de estado para controlar a exibição do pop-up de exclusão
    var estrategiaParaExcluir by remember { mutableStateOf<String?>(null) }

    val estrategiasFiltradas = estrategias.filter {
        it["titulo"].toString().contains(searchText, ignoreCase = true) ||
                it["texto"].toString().contains(searchText, ignoreCase = true) ||
                it["departamento"].toString().contains(searchText, ignoreCase = true)
    }

    Scaffold(
        bottomBar = { BottomMenu(navController = navController) },
        floatingActionButton = {
            if (podeEditar) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nova Estratégia")
                }
            }
        }
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
                text = if (podeEditar) "Gestão de Estratégias" else "Estratégias",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Buscar estratégia", color = Color.Gray) },
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
            } else if (estrategiasFiltradas.isEmpty()) {
                Text("Nenhuma estratégia encontrada.", color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(estrategiasFiltradas) { estrategia ->
                        StrategyListItem(
                            estrategia = estrategia,
                            podeEditar = podeEditar,
                            onEditarClick = { estrategiaEditando = estrategia },
                            // 2. Aciona a abertura do pop-up guardando o ID
                            onExcluirClick = { id -> estrategiaParaExcluir = id }
                        )
                    }
                }
            }
        }

        if (showCreateDialog) {
            StrategyFormDialog(
                tituloForm = "Nova estratégia",
                onDismiss = { showCreateDialog = false },
                onSalvar = { titulo, depto, texto ->
                    viewModel.criarEstrategia(titulo, depto, texto)
                    showCreateDialog = false
                }
            )
        }

        estrategiaEditando?.let { estrategia ->
            StrategyFormDialog(
                tituloForm = "Editar estratégia",
                dadosIniciais = estrategia,
                onDismiss = { estrategiaEditando = null },
                onSalvar = { titulo, depto, texto ->
                    val id = estrategia["id"] as? String ?: return@StrategyFormDialog
                    viewModel.atualizarEstrategia(id, titulo, depto, texto)
                    estrategiaEditando = null
                }
            )
        }

        // =========================================================
        // 3. POP-UP DE CONFIRMAÇÃO DE EXCLUSÃO
        // =========================================================
        estrategiaParaExcluir?.let { idParaDeletar ->
            AlertDialog(
                onDismissRequest = { estrategiaParaExcluir = null },
                title = {
                    Text(
                        text = "Excluir Estratégia?",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF44336) // Vermelho alerta
                    )
                },
                text = {
                    Text(
                        text = "Você tem certeza que deseja excluir esta estratégia? Esta ação é permanente e não poderá ser desfeita.",
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.excluirEstrategia(idParaDeletar) // Exclui no Firebase
                            estrategiaParaExcluir = null // Fecha o modal
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) {
                        Text("Sim, Excluir")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { estrategiaParaExcluir = null }
                    ) {
                        Text("Cancelar", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun StrategyFormDialog(
    tituloForm: String,
    dadosIniciais: Map<String, Any>? = null,
    onDismiss: () -> Unit,
    onSalvar: (titulo: String, departamento: String, texto: String) -> Unit
) {
    var titulo by remember { mutableStateOf(dadosIniciais?.get("titulo") as? String ?: "") }
    var depto by remember { mutableStateOf(dadosIniciais?.get("departamento") as? String ?: "") }
    var texto by remember { mutableStateOf(dadosIniciais?.get("texto") as? String ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tituloForm, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = depto, onValueChange = { depto = it }, label = { Text("Departamento") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = texto, onValueChange = { texto = it }, label = { Text("Descrição detalhada") }, modifier = Modifier.fillMaxWidth().height(100.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank() && texto.isNotBlank()) {
                        onSalvar(titulo, depto, texto)
                    }
                }
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) }
        }
    )
}

@Composable
fun StrategyListItem(
    estrategia: Map<String, Any>,
    podeEditar: Boolean,
    onEditarClick: () -> Unit,
    onExcluirClick: (String) -> Unit
) {
    val id = estrategia["id"] as? String ?: ""
    val titulo = estrategia["titulo"] as? String ?: "Estratégia"
    val textoCompleto = estrategia["texto"] as? String ?: "Sem descrição."
    val depto = estrategia["departamento"] as? String ?: "Todos"

    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFFF5F6F8), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))

                    if (podeEditar) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = onEditarClick, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
                            }
                            IconButton(onClick = { onExcluirClick(id) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                StrategyDetailRow("Depto:", depto)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (expanded) "Ocultar detalhes" else "Ver detalhes",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { expanded = !expanded }
                )

                AnimatedVisibility(visible = expanded) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFF5F6F8), RoundedCornerShape(8.dp)).padding(12.dp)) {
                            Text(text = textoCompleto, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)
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
fun StrategyDetailRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("• ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = "$label ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = value, fontSize = 14.sp, color = Color.DarkGray)
    }
}