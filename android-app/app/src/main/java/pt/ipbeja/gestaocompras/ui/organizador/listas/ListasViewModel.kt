package pt.ipbeja.gestaocompras.ui.organizador.listas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipbeja.gestaocompras.data.local.dao.ListaResumoRow
import pt.ipbeja.gestaocompras.data.local.entity.ListaCompraEntity
import pt.ipbeja.gestaocompras.data.repository.ComprasRepository

data class ListaExibicao(
    val id: Long,
    val nome: String,
    val itens: Int,
    val nomeLoja: String?,
    val totalEstimado: Double
)

class ListasViewModel(
    private val repo: ComprasRepository
) : ViewModel() {

    val listas: StateFlow<List<ListaExibicao>> =
        combine(repo.resumosDeListasAtivas(), repo.lojas()) { resumos, lojas ->
            val mapaLojas = lojas.associateBy { it.id }
            resumos.map { r ->
                ListaExibicao(
                    id = r.id,
                    nome = r.nome,
                    itens = r.itens,
                    nomeLoja = r.lojaId?.let { mapaLojas[it]?.nome },
                    totalEstimado = r.totalEstimado
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun criarLista(nome: String) {
        val n = nome.trim().ifBlank { "Nova lista" }
        viewModelScope.launch {
            repo.guardarLista(ListaCompraEntity(nome = n))
        }
    }

    fun apagarLista(id: Long) {
        viewModelScope.launch {
            repo.obterLista(id)?.let { repo.apagarLista(it) }
        }
    }
}
