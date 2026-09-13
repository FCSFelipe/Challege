package br.com.fiap.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import br.com.fiap.myapplication.R

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val autentica = FirebaseAuth.getInstance()
    val focusManager = LocalFocusManager.current

    fun efetuarLogin() {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            isLoading = true
            focusManager.clearFocus()

            autentica.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    isLoading = false
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
                .addOnFailureListener {
                    isLoading = false
                    Toast.makeText(context, "Erro ao fazer login", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.drawable.logotipo),
            contentDescription = "Logotipo da Empresa",
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(100.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),

            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color.Gray,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    efetuarLogin()
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color.Gray,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { efetuarLogin() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Entrar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate("signup") },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Não tem uma conta? Cadastre-se")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}