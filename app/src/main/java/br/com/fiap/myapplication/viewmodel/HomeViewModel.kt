package br.com.fiap.myapplication.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {

    private val autentica = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Estado original para a permissão de acesso
    private val _userRole = mutableStateOf("")
    val userRole: State<String> = _userRole

    // NOVO: Estado para armazenar e atualizar o nome do usuário na Home
    private val _userName = mutableStateOf("")
    val userName: State<String> = _userName

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        // Chamada da nova função reativa
        buscarDadosEmTempoReal()
    }

    private fun buscarDadosEmTempoReal() {
        val currentUser = autentica.currentUser
        if (currentUser != null) {
            // O addSnapshotListener fica "escutando" o banco. Se você mudar o nome no Perfil, ele atualiza aqui na hora!
            db.collection("users").document(currentUser.uid)
                .addSnapshotListener { document, erro ->
                    if (erro == null && document != null && document.exists()) {

                        // 1. Atualiza a permissão de acesso
                        _userRole.value = document.getString("role") ?: "OPERADOR"

                        // 2. Atualiza o nome do usuário (lendo as chaves que você definiu no cadastro/perfil)
                        _userName.value = document.getString("nome") ?: document.getString("name") ?: "Usuário"

                        _isLoading.value = false
                    } else {
                        // Fallback em caso de erro na conexão
                        _userRole.value = "OPERADOR"
                        _userName.value = "Usuário"
                        _isLoading.value = false
                    }
                }
        } else {
            _isLoading.value = false
        }
    }

    fun fazerLogout() {
        autentica.signOut()
    }
}