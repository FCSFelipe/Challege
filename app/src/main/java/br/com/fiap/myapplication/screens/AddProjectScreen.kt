package br.com.fiap.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.fiap.myapplication.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectScreen(
    navController: NavController,
    ideaId: String,
    ideaTitle: String
) {
    var investimento by remember { mutableStateOf("") }
    var prazo by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.aprovar_e_criar_projeto), color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = stringResource(R.string.ideia_original), style = MaterialTheme.typography.labelLarge)
            Text(text = ideaTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = stringResource(R.string.defina_os_parametros_de_execucao_deste_projeto), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = investimento,
                onValueChange = { investimento = it },
                label = { Text("Investimento Aprovado (R$)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = prazo,
                onValueChange = { prazo = it },
                label = { Text("Prazo Estimado (ex: 3 meses)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (investimento.isNotEmpty() && prazo.isNotEmpty()) {
                        isLoading = true
                        focusManager.clearFocus()

                        val projectId = UUID.randomUUID().toString()
                        val valorInvestimento = investimento.replace(",", ".").toDoubleOrNull() ?: 0.0

                        val novoProjeto = hashMapOf(
                            "id" to projectId,
                            "ideaId" to ideaId,
                            "titulo" to ideaTitle,
                            "investimento" to valorInvestimento,
                            "prazo" to prazo,
                            "status" to "EM EXECUÇÃO"
                        )

                        db.collection("projetos").document(projectId).set(novoProjeto)
                            .addOnSuccessListener {
                                db.collection("ideias").document(ideaId).update("status", "APROVADA")
                                    .addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(context,
                                            context.getString(R.string.projeto_iniciado_com_sucesso), Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    }
                            }
                            .addOnFailureListener {
                                isLoading = false
                                Toast.makeText(context, "Erro ao criar projeto", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(stringResource(R.string.confirmar_inicio_do_projeto))
                }
            }
        }
    }
}