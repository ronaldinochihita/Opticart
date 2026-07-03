package pt.ipbeja.gestaocompras.ui.organizador.historico

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
import pt.ipbeja.gestaocompras.data.local.entity.HistoricoPrecoEntity
import pt.ipbeja.gestaocompras.data.repository.ComprasRepository

@OptIn(ExperimentalCoroutinesApi::class)
class HistoricoViewModel(
    private val repo: ComprasRepository
) : ViewModel() {

    val produtos: StateFlow<List<String>> = repo.produtosComHistorico()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _produtoSelecionado = MutableStateFlow<String?>(null)
    val produtoSelecionado: StateFlow<String?> = _produtoSelecionado

    val comparacao: StateFlow<List<HistoricoPrecoEntity>> =
        _produtoSelecionado.flatMapLatest { produto ->
            if (produto == null) flowOf(emptyList())
            else repo.precosPorProduto(produto)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val evolucao: StateFlow<List<HistoricoPrecoEntity>> =
        _produtoSelecionado.flatMapLatest { produto ->
            if (produto == null) flowOf(emptyList())
            else repo.precosPorProduto(produto)  // usa todos os pontos p/ gráfico
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        // Selecionar automaticamente o primeiro produto disponível.
        viewModelScope.launch {
            produtos.collect { lista ->
                if (_produtoSelecionado.value == null && lista.isNotEmpty()) {
                    _produtoSelecionado.value = lista.first()
                }
            }
        }
    }

    fun selecionar(produto: String) {
        _produtoSelecionado.value = produto
    }
}
