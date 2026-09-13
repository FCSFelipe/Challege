package br.com.fiap.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.myapplication.R
import br.com.fiap.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var perfilSelecionado by remember { mutableStateOf("OPERADOR") }

    val autentica = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    var estaCarregando by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    fun efetuarCadastro() {
        if (nome.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            estaCarregando = true
            focusManager.clearFocus()

            autentica.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { tarefa ->
                    if (tarefa.isSuccessful) {
                        val userId = tarefa.result?.user?.uid

                        if (userId != null) {
                            val userMap = hashMapOf(
                                "id" to userId,
                                "nome" to nome,
                                "email" to email,
                                "role" to perfilSelecionado
                            )

                            db.collection("users").document(userId).set(userMap)
                                .addOnSuccessListener {
                                    estaCarregando = false
                                    Toast.makeText(context, "Cadastro criado com sucesso! Realize o login.", Toast.LENGTH_LONG).show()

                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                                .addOnFailureListener { erro ->
                                    estaCarregando = false
                                    Toast.makeText(context, "Erro ao salvar dados: ${erro.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    } else {
                        estaCarregando = false
                        Toast.makeText(context, "Erro: ${tarefa.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = modifier.height(32.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logotipo),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(100.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = modifier.height(24.dp))
            Text(
                text = "Faça seu cadastro:",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier.padding(8.dp)
            )

        }
        Spacer(modifier = modifier.height(16.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text(text = "Nome Completo") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "E-mail") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    efetuarCadastro()
                }
            )
        )
        Spacer(modifier = modifier.height(16.dp))

        Text(text = "Selecione seu perfil na empresa:", fontSize = 14.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RadioButton(
                    selected = perfilSelecionado == "OPERADOR",
                    onClick = { perfilSelecionado = "OPERADOR" }
                )
                Text("Operador", fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RadioButton(
                    selected = perfilSelecionado == "GESTOR",
                    onClick = { perfilSelecionado = "GESTOR" }
                )
                Text("Gestor", fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RadioButton(
                    selected = perfilSelecionado == "LIDERANCA",
                    onClick = { perfilSelecionado = "LIDERANCA" }
                )
                Text("Liderança", fontSize = 12.sp)
            }
        }

        Spacer(modifier = modifier.height(24.dp))

        Button(
            onClick = { efetuarCadastro() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (estaCarregando){
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(text = "Criar conta")
            }
        }

        Spacer(modifier = modifier.height(8.dp))

        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text("Já tem uma conta? Entrar")
        }

        Spacer(modifier = modifier.height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun SignupScreenPreview() {
    MyApplicationTheme () {
        SignupScreen(
            modifier = Modifier,
            navController = rememberNavController()
        )
    }
}