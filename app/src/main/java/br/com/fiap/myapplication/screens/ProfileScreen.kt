package br.com.fiap.myapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import br.com.fiap.myapplication.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val nomeBanco by viewModel.nome
    val enderecoBanco by viewModel.endereco
    val telefoneBanco by viewModel.telefone
    val isLoading by viewModel.isLoading
    val isSalvoComSucesso by viewModel.isSalvoComSucesso

    var nomeInput by remember { mutableStateOf("") }
    var enderecoInput by remember { mutableStateOf("") }
    var telefoneInput by remember { mutableStateOf("") }

    LaunchedEffect(nomeBanco, enderecoBanco, telefoneBanco) {
        nomeInput = nomeBanco
        enderecoInput = enderecoBanco
        telefoneInput = telefoneBanco
    }

    val snackbarHostState = remember { SnackbarHostState() }

    if (isSalvoComSucesso) {
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar("Perfil atualizado com sucesso!")
            viewModel.limparStatusSucesso()
        }
    }

    Scaffold(
        bottomBar = { BottomMenu(navController = navController) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Meu Perfil",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFF5F6F8), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar de usuário",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading && nomeBanco.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = nomeInput,
                        onValueChange = { nomeInput = it },
                        label = { Text("Nome Completo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = telefoneInput,
                        onValueChange = { telefoneInput = it },
                        label = { Text("Telefone / Celular") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = enderecoInput,
                        onValueChange = { enderecoInput = it },
                        label = { Text("Endereço / Região") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.atualizarPerfil(nomeInput, enderecoInput, telefoneInput)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Salvar Alterações",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.fazerLogout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFF44336)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFF44336).copy(alpha = 0.5f))
                    )
                ) {
                    Text(
                        text = "Sair da Conta",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}