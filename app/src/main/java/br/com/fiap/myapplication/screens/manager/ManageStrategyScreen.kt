package br.com.fiap.myapplication.screens.manager

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
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
    var estrategiaParaExcluir by remember { mutableStateOf<String?>(null) }

    val estrategiasFiltradas = estrategias.filter {
        it["titulo"].toString().contains(searchText, ignoreCase = true) ||
                it["texto"].toString().contains(searchText, ignoreCase = true) ||
                it["departamento"].toString().contains(searchText, ignoreCase = true)
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
        bottomBar = { BottomMenu(navController = navController) },
        floatingActionButton = {
            if (podeEditar) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = CircleShape
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
                .background(Color(0xFFF8F9FA))
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (podeEditar) "Gestão de Estratégias" else "Estratégias",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Buscar estratégia", color = Color.Gray) },
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
            } else if (estrategiasFiltradas.isEmpty()) {
                Text("Nenhuma estratégia encontrada.", color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(estrategiasFiltradas) { estrategia ->
                        StrategyListItem(
                            estrategia = estrategia,
                            podeEditar = podeEditar,
                            onEditarClick = { estrategiaEditando = estrategia },
                            onExcluirClick = { id -> estrategiaParaExcluir = id }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
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

        estrategiaParaExcluir?.let { idParaDeletar ->
            AlertDialog(
                onDismissRequest = { estrategiaParaExcluir = null },
                title = {
                    Text(
                        text = "Excluir Estratégia?",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF44336)
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
                            viewModel.excluirEstrategia(idParaDeletar)
                            estrategiaParaExcluir = null
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFFF0F5FF), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountTree,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = titulo,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = depto,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                if (podeEditar) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                .clickable { onEditarClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Editar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .border(1.dp, Color(0xFFF44336), CircleShape)
                                .clickable { onExcluirClick(id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Excluir",
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { expanded = !expanded }
            ) {
                Text(
                    text = if (expanded) "Ocultar detalhes" else "Ver detalhes",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = textoCompleto,
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}