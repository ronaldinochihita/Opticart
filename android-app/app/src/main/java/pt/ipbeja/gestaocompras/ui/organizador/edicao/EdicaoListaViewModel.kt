package pt.ipbeja.gestaocompras.ui.organizador.edicao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipbeja.gestaocompras.data.local.entity.ItemListaEntity
import pt.ipbeja.gestaocompras.data.local.entity.ListaCompraEntity
import pt.ipbeja.gestaocompras.data.local.entity.LojaEntity
import pt.ipbeja.gestaocompras.data.repository.ComprasRepository

data class EdicaoEstado(
    val lista: ListaCompraEntity? = null,
    val loja: LojaEntity? = null,
    val itens: List<ItemListaEntity> = emptyList(),
    val totalEstimado: Double = 0.0
)

@OptIn(ExperimentalCoroutinesApi::class)
class EdicaoListaViewModel(
    private val repo: ComprasRepository
) : ViewModel() {

    private val _listaId = MutableStateFlow<Long?>(null)

    val estado: StateFlow<EdicaoEstado> = _listaId
        .flatMapLatest { listaId ->
            if (listaId == null) flowOf(EdicaoEstado())
            else combine(
                repo.itensDaLista(listaId),
                repo.lojas()
            ) { itens, lojas ->
                val lista = repo.obterLista(listaId) ?: return@combine EdicaoEstado()
                val loja = lista.lojaId?.let { id -> lojas.firstOrNull { it.id == id } }
                EdicaoEstado(
                    lista = lista,
                    loja = loja,
                    itens = itens,
                    totalEstimado = itens.sumOf { (it.precoUnitario ?: 0.0) * it.quantidade }
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EdicaoEstado())

    val lojas: StateFlow<List<LojaEntity>> = repo.lojas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun carregar(listaId: Long) { _listaId.value = listaId }

    fun alternarComprado(item: ItemListaEntity) {
        viewModelScope.launch {
            repo.marcarItemNoCarrinho(item.id, !item.noCarrinho)
        }
    }

    fun adicionarItem(descricao: String, quantidade: Double, unidade: String?, preco: Double?) {
        val desc = descricao.trim()
        val listaId = _listaId.value ?: return
        if (desc.isBlank()) return
        viewModelScope.launch {
            repo.guardarItem(
                ItemListaEntity(
                    listaId = listaId,
                    descricao = desc,
                    quantidade = quantidade.coerceAtLeast(1.0),
                    unidade = unidade?.trim()?.ifBlank { null } ?: "un",
                    precoUnitario = preco
                )
            )
        }
    }

    fun apagarItem(item: ItemListaEntity) {
        viewModelScope.launch { repo.apagarItem(item) }
    }

    fun escolherLoja(loja: LojaEntity?) {
        val lista = estado.value.lista ?: return
        viewModelScope.launch {
            repo.atualizarLista(lista.copy(lojaId = loja?.id))
        }
    }
}
