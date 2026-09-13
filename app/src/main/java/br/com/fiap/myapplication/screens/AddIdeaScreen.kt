package br.com.fiap.myapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.fiap.myapplication.R
import br.com.fiap.myapplication.components.BottomMenu
import br.com.fiap.myapplication.ui.theme.poppinsFamily
import br.com.fiap.myapplication.viewmodel.OperatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIdeaScreen(
    navController: NavController,
    viewModel: OperatorViewModel = viewModel()
) {
    var titulo by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Desenvolvimento de Produto") }
    var problema by remember { mutableStateOf("") }
    var solucao by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    val categoriasDisponiveis = listOf("Desenvolvimento de Produto", "Inovação Digital", "Eficiência Energética", "Melhoria de Processo")

    val focusManager = LocalFocusManager.current
    val isLoading by viewModel.isLoading

    Scaffold(
        topBar = { },
        bottomBar = { BottomMenu(navController = navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.nova_ideia),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                FormItemWithLabel(label = "Título") {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.titulo_da_ideia)) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        colors = outlinedTextFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                FormItemWithLabel(label = "Categoria") {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = categoria,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            categoriasDisponiveis.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
                                    onClick = {
                                        categoria = item
                                        expanded = false
                                        focusManager.moveFocus(FocusDirection.Down)
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                FormItemWithLabel(label = "Problema encontrado") {
                    OutlinedTextField(
                        value = problema,
                        onValueChange = { problema = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.descreva_o_problema)) },
                        minLines = 4,
                        maxLines = 8,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        colors = outlinedTextFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                FormItemWithLabel(label = "Solução sugerida") {
                    OutlinedTextField(
                        value = solucao,
                        onValueChange = { solucao = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.descreva_a_solucao)) },
                        minLines = 4,
                        maxLines = 8,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        colors = outlinedTextFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.salvarNovaIdeia(titulo, categoria, problema, solucao) {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text(stringResource(R.string.salvar), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun FormItemWithLabel(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(4.dp))
        content()
    }
}

@Composable
fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = Color.Gray,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = Color.Gray,
    cursorColor = MaterialTheme.colorScheme.primary
)