package br.com.fiap.myapplication.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class LeadershipViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    val roi = mutableStateOf("0%")
    val lucro = mutableStateOf("R$ 0")
    val quantidadeProjetosAtivos = mutableStateOf(0)

    private val _projetos = mutableStateOf<List<Map<String, Any>>>(emptyList())
    val projetos: State<List<Map<String, Any>>> = _projetos

    init {
        buscarProjetosECalcularMetricas()
    }

    private fun buscarProjetosECalcularMetricas() {
        db.collection("projetos")
            .addSnapshotListener { snapshot, erro ->
                if (erro == null && snapshot != null) {

                    val listaProjetos = snapshot.documents.mapNotNull { doc ->
                        doc.data?.plus("id" to doc.id)
                    }

                    _projetos.value = listaProjetos
                    quantidadeProjetosAtivos.value = listaProjetos.size

                    var somaLucro = 0.0
                    var somaInvestimento = 0.0

                    for (projeto in listaProjetos) {
                        val lucroStr = projeto["lucro"]?.toString() ?: "0"
                        val investStr = projeto["investimento"]?.toString() ?: "0"

                        val lucroValor = lucroStr.replace(",", ".").replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
                        val investValor = investStr.replace(",", ".").replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0

                        somaLucro += lucroValor
                        somaInvestimento += investValor
                    }

                    lucro.value = "R$ ${somaLucro.toInt()}"

                    val roiCalculado = if (somaInvestimento > 0) {
                        ((somaLucro / somaInvestimento) * 100).toInt()
                    } else {
                        0
                    }
                    roi.value = "$roiCalculado%"
                }
            }
    }
}