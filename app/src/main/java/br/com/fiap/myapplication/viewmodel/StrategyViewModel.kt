package br.com.fiap.myapplication.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class StrategyViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _estrategias = mutableStateOf<List<Map<String, Any>>>(emptyList())
    val estrategias: State<List<Map<String, Any>>> = _estrategias

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _podeEditar = mutableStateOf(false)
    val podeEditar: State<Boolean> = _podeEditar

    init {
        verificarPermissaoERequest()
    }

    private fun verificarPermissaoERequest() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            buscarEstrategias()
            return
        }

        _isLoading.value = true

        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                val role = document.getString("role")?.uppercase() ?: ""
                val cargo = document.getString("cargo")?.uppercase() ?: ""

                val isGestor = role.contains("GESTOR") || cargo.contains("GESTOR")
                val isLider = role.contains("LIDER") || cargo.contains("LIDER")

                _podeEditar.value = isGestor || isLider

                buscarEstrategias()
            }
            .addOnFailureListener {
                _podeEditar.value = false
                buscarEstrategias()
            }
    }

    private fun buscarEstrategias() {
        db.collection("diretrizes")
            .orderBy("dataCriacao", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                _isLoading.value = false
                if (snapshot != null) {
                    _estrategias.value = snapshot.documents.mapNotNull { doc ->
                        doc.data?.plus("id" to doc.id)
                    }
                }
            }
    }

    fun criarEstrategia(titulo: String, departamento: String, texto: String) {
        if (!_podeEditar.value) return

        val novaDiretriz = hashMapOf(
            "titulo" to titulo,
            "departamento" to departamento,
            "texto" to texto,
            "dataCriacao" to System.currentTimeMillis()
        )
        db.collection("diretrizes").add(novaDiretriz)
    }

    fun atualizarEstrategia(id: String, titulo: String, departamento: String, texto: String) {
        if (!_podeEditar.value || id.isEmpty()) return

        val updates = mapOf(
            "titulo" to titulo,
            "departamento" to departamento,
            "texto" to texto
        )
        db.collection("diretrizes").document(id).update(updates)
    }

    fun excluirEstrategia(id: String) {
        if (!_podeEditar.value || id.isEmpty()) return

        db.collection("diretrizes").document(id).delete()
    }
}