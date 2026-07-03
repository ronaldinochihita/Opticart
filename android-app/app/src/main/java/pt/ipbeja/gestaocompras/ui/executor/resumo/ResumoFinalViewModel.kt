package pt.ipbeja.gestaocompras.ui.executor.resumo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipbeja.gestaocompras.data.local.entity.HistoricoCompraEntity
import pt.ipbeja.gestaocompras.data.local.entity.HistoricoPrecoEntity
import pt.ipbeja.gestaocompras.data.local.entity.ItemListaEntity
import pt.ipbeja.gestaocompras.data.local.entity.ListaCompraEntity
import pt.ipbeja.gestaocompras.data.local.entity.LojaEntity
import pt.ipbeja.gestaocompras.data.repository.ComprasRepository
import pt.ipbeja.gestaocompras.ui.executor.modo.ModoCompraViewModel

data class ResumoFinalEstado(
    val lista: ListaCompraEntity? = null,
    val loja: LojaEntity? = null,
    val itensCarrinho: List<ItemListaEntity> = emptyList(),
    val itensSemPreco: List<ItemListaEntity> = emptyList(),
    val totalReal: Double = 0.0,
    val totalArredondado: Double = 0.0
)

@OptIn(ExperimentalCoroutinesApi::class)
class ResumoFinalViewModel(
    private val repo: ComprasRepository
) : ViewModel() {

    private val _listaId = MutableStateFlow<Long?>(null)

    val estado: StateFlow<ResumoFinalEstado> = _listaId
        .flatMapLatest { id ->
            if (id == null) flowOf(ResumoFinalEstado())
            else kotlinx.coroutines.flow.flow {
                val lista = repo.obterLista(id)
                val loja = lista?.lojaId?.let { repo.obterLoja(it) }
                repo.itensDaLista(id).collect { itens ->
                    val carrinho = itens.filter { it.noCarrinho }
                    val semPreco = carrinho.filter { it.precoUnitario == null }
                    val totalReal = carrinho.sumOf { (it.precoUnitario ?: 0.0) * it.quantidade }
                    emit(
                        ResumoFinalEstado(
                            lista = lista,
                            loja = loja,
                            itensCarrinho = carrinho,
                            itensSemPreco = semPreco,
                            totalReal = totalReal,
                            totalArredondado = ModoCompraViewModel.arredondar5Cent(totalReal)
                        )
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ResumoFinalEstado())

    fun carregar(listaId: Long) { _listaId.value = listaId }

    /**
     * Finaliza a compra:
     *   • grava o histórico da compra (para efeitos de relatório futuro)
     *   • grava um novo ponto no histórico de preços por cada item comprado com preço
     *   • marca a lista como concluída
     */
    /**
     * Finaliza a compra.
     *
     * As escritas correm dentro de <code>NonCancellable</code> para garantir
     * que não são interrompidas quando a navegação faz <code>popBackStack</code>
     * (o ViewModel é limpo em seguida). O <code>onDone()</code> só é chamado
     * depois dos <code>INSERT</code>s terem terminado, para que ao voltar ao
     * ecrã Histórico os produtos apareçam imediatamente.
     *
     * Devolve o número de preços gravados no histórico (0 se a lista não
     * tinha loja associada — nesse caso o ecrã mostra um aviso).
     */
    fun fechar(onDone: (precosGravados: Int) -> Unit) {
        val estadoAtual = estado.value
        val lista = estadoAtual.lista ?: return
        viewModelScope.launch {
            val precosGravados = withContext(NonCancellable) {
                repo.registarHistoricoCompra(
                    HistoricoCompraEntity(
                        listaId = lista.id,
                        nomeLista = lista.nome,
                        nomeLoja = estadoAtual.loja?.nome,
                        totalEstimado = estadoAtual.totalReal,
                        totalPago = estadoAtual.totalArredondado,
                        arredondamento = estadoAtual.totalArredondado - estadoAtual.totalReal
                    )
                )

                val agora = System.currentTimeMillis()
                val gravados = estadoAtual.loja?.let { loja ->
                    val registos = estadoAtual.itensCarrinho
                        .filter { it.precoUnitario != null }
                        .map { item ->
                            HistoricoPrecoEntity(
                                produtoNome = item.descricao,
                                lojaId = loja.id,
                                nomeLoja = loja.nome,
                                preco = item.precoUnitario!!,
                                data = agora
                            )
                        }
                    if (registos.isNotEmpty()) {
                        repo.registarPrecos(registos)
                        registos.size
                    } else 0
                } ?: 0

                repo.concluirLista(lista.id)
                gravados
            }
            onDone(precosGravados)
        }
    }
}
