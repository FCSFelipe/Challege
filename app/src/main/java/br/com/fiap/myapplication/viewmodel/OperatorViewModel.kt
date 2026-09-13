package br.com.fiap.myapplication.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OperatorViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _minhasIdeias = mutableStateOf<List<Map<String, Any>>>(emptyList())
    val minhasIdeias: State<List<Map<String, Any>>> = _minhasIdeias

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _estrategiaAtual = mutableStateOf("Aguardando diretrizes da liderança...")
    val estrategiaAtual: State<String> = _estrategiaAtual

    init {
        buscarMinhasIdeias()
        db.collection("diretrizes")
            .orderBy("dataCriacao", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, erro ->
                if (erro == null && snapshot != null && !snapshot.isEmpty) {
                    _estrategiaAtual.value = snapshot.documents[0].getString("texto") ?: ""
                } else {
                    _estrategiaAtual.value = "Nenhuma diretriz ativa no momento."
                }
            }
    }

    private fun buscarMinhasIdeias() {
        if (userId.isNotEmpty()) {
            db.collection("ideias")
                .whereEqualTo("autorId", userId)
                .addSnapshotListener { snapshot, erro ->
                    if (erro != null) {
                        _isLoading.value = false
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        _minhasIdeias.value = snapshot.documents.mapNotNull { it.data }
                    }
                    _isLoading.value = false
                }
        } else {
            _isLoading.value = false
        }
    }
    fun salvarNovaIdeia(
        titulo: String,
        categoria: String,
        problema: String,
        solucao: String,
        onSuccess: () -> Unit
    ) {
        _isLoading.value = true

        val novaIdeia = hashMapOf(
            "titulo" to titulo,
            "categoria" to categoria,
            "problema" to problema,
            "solucao" to solucao,
            "status" to "PENDENTE",
            "autorId" to userId
        )

        db.collection("ideias")
            .add(novaIdeia)
            .addOnSuccessListener {
                _isLoading.value = false
                onSuccess()
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }
}