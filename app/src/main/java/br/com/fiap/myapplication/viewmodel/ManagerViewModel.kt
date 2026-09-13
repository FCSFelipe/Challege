package br.com.fiap.myapplication.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ManagerViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _todasIdeias = mutableStateOf<List<Map<String, Any>>>(emptyList())
    val todasIdeias: State<List<Map<String, Any>>> = _todasIdeias

    private val _projetos = mutableStateOf<List<Map<String, Any>>>(emptyList())
    val projetos: State<List<Map<String, Any>>> = _projetos

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun buscarTodasIdeias() {
        _isLoading.value = true
        db.collection("ideias").addSnapshotListener { snapshot, erro ->
            if (erro == null && snapshot != null) {
                _todasIdeias.value = snapshot.documents.mapNotNull { doc ->
                    doc.data?.plus("id" to doc.id)
                }
            }
            _isLoading.value = false
        }
    }

    fun atualizarStatusIdeia(ideiaId: String, novoStatus: String) {
        if (ideiaId.isNotEmpty()) {
            db.collection("ideias").document(ideiaId).update("status", novoStatus)
        }
    }

    fun atualizarPrioridadeIdeia(ideiaId: String, novaPrioridade: String) {
        if (ideiaId.isNotEmpty()) {
            db.collection("ideias").document(ideiaId).update("prioridade", novaPrioridade)
        }
    }

    fun buscarProjetos() {
        _isLoading.value = true
        db.collection("projetos").addSnapshotListener { snapshot, erro ->
            if (erro == null && snapshot != null) {
                _projetos.value = snapshot.documents.mapNotNull { doc ->
                    doc.data?.plus("id" to doc.id)
                }
            }
            _isLoading.value = false
        }
    }

    fun cadastrarProjeto(nome: String, descricao: String) {
        val novoProjeto = hashMapOf(
            "nome" to nome,
            "descricao" to descricao,
            "progresso" to 0f,
            "resultados" to "Aguardando início...",
            "roi" to "Em cálculo",
            "lucro" to "Em cálculo",
            "investimento" to "A definir",
            "prazo" to "A definir",
            "dataCriacao" to System.currentTimeMillis()
        )
        db.collection("projetos").add(novoProjeto)
    }

    fun atualizarProjeto(
        projetoId: String,
        progresso: Float,
        resultados: String,
        lucro: String,
        investimento: String,
        prazo: String
    ) {
        if (projetoId.isEmpty()) return

        val lucroLimpo = lucro.replace(",", ".").replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
        val investimentoLimpo = investimento.replace(",", ".").replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0

        val roiCalculado = if (investimentoLimpo > 0.0) {
            val calculo = (lucroLimpo / investimentoLimpo) * 100
            "${calculo.toInt()}%"
        } else {
            "0%"
        }

        val updates = mapOf(
            "progresso" to progresso,
            "resultados" to resultados,
            "roi" to roiCalculado,
            "lucro" to lucro,
            "investimento" to investimento,
            "prazo" to prazo
        )

        db.collection("projetos").document(projetoId).update(updates)
    }
}