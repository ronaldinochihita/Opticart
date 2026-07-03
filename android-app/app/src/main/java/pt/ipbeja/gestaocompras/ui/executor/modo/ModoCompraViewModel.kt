package pt.ipbeja.gestaocompras.ui.executor.modo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipbeja.gestaocompras.data.local.entity.ItemListaEntity
import pt.ipbeja.gestaocompras.data.local.entity.ListaCompraEntity
import pt.ipbeja.gestaocompras.data.repository.ComprasRepository
import kotlin.math.ceil

data class ModoCompraEstado(
    val lista: ListaCompraEntity? = null,
    val porAdicionar: List<ItemListaEntity> = emptyList(),
    val noCarrinho: List<ItemListaEntity> = emptyList(),
    val totalReal: Double = 0.0,
    val totalArredondado: Double = 0.0
)

@OptIn(ExperimentalCoroutinesApi::class)
class ModoCompraViewModel(
    private val repo: ComprasRepository
) : ViewModel() {

    private val _listaId = MutableStateFlow<Long?>(null)

    val estado: StateFlow<ModoCompraEstado> = _listaId
        .flatMapLatest { id ->
            if (id == null) flowOf(ModoCompraEstado())
            else repo.itensDaLista(id).let { fluxo ->
                fluxo
            }.let { itens ->
                kotlinx.coroutines.flow.flow {
                    val lista = repo.obterLista(id)
                    itens.collect { todos ->
                        val por = todos.filter { !it.noCarrinho }
                        val no = todos.filter { it.noCarrinho }
                        val totalReal = no.sumOf { (it.precoUnitario ?: 0.0) * it.quantidade }
                        emit(
                            ModoCompraEstado(
                                lista = lista,
                                porAdicionar = por,
                                noCarrinho = no,
                                totalReal = totalReal,
                                totalArredondado = arredondar5Cent(totalReal)
                            )
                        )
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ModoCompraEstado())

    fun carregar(listaId: Long) { _listaId.value = listaId }

    fun marcar(item: ItemListaEntity, noCarrinho: Boolean) {
        viewModelScope.launch {
            repo.marcarItemNoCarrinho(item.id, noCarrinho)
        }
    }

    fun sair() {
        viewModelScope.launch {
            // Ao sair repor todos os itens como "por adicionar" para não deixar estado inconsistente.
            estado.value.noCarrinho.forEach { repo.marcarItemNoCarrinho(it.id, false) }
        }
    }

    companion object {
        /** Arredonda para o múltiplo de 5 cêntimos mais próximo por excesso. */
        fun arredondar5Cent(valor: Double): Double {
            if (valor <= 0.0) return 0.0
            return ceil(valor * 20.0) / 20.0
        }
    }
}
