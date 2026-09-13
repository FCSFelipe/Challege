package br.com.fiap.myapplication.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val uid = auth.currentUser?.uid ?: ""

    private val _nome = mutableStateOf("")
    val nome: State<String> = _nome

    private val _endereco = mutableStateOf("")
    val endereco: State<String> = _endereco

    private val _telefone = mutableStateOf("")
    val telefone: State<String> = _telefone

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _isSalvoComSucesso = mutableStateOf(false)
    val isSalvoComSucesso: State<Boolean> = _isSalvoComSucesso

    init {
        carregarDadosPerfil()
    }

    fun carregarDadosPerfil() {
        if (uid.isEmpty()) return
        _isLoading.value = true

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    _nome.value = document.getString("nome") ?: document.getString("name") ?: ""
                    _endereco.value = document.getString("endereco") ?: ""
                    _telefone.value = document.getString("telefone") ?: ""
                }
                _isLoading.value = false
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }

    fun atualizarPerfil(novoNome: String, novoEndereco: String, novoTelefone: String) {
        if (uid.isEmpty()) return
        _isLoading.value = true
        _isSalvoComSucesso.value = false

        val dadosAtualizados = mapOf(
            "nome" to novoNome,
            "name" to novoNome,
            "endereco" to novoEndereco,
            "telefone" to novoTelefone
        )

        db.collection("users").document(uid).update(dadosAtualizados)
            .addOnSuccessListener {
                _nome.value = novoNome
                _endereco.value = novoEndereco
                _telefone.value = novoTelefone
                _isSalvoComSucesso.value = true
                _isLoading.value = false
            }
            .addOnFailureListener {
                db.collection("users").document(uid).set(dadosAtualizados)
                    .addOnSuccessListener {
                        _nome.value = novoNome
                        _endereco.value = novoEndereco
                        _telefone.value = novoTelefone
                        _isSalvoComSucesso.value = true
                        _isLoading.value = false
                    }
                    .addOnFailureListener {
                        _isLoading.value = false
                    }
            }
    }

    fun fazerLogout() {
        auth.signOut()
    }

    fun limparStatusSucesso() {
        _isSalvoComSucesso.value = false
    }
}