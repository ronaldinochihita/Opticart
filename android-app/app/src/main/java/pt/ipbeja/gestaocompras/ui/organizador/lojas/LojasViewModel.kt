package pt.ipbeja.gestaocompras.ui.organizador.lojas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipbeja.gestaocompras.data.local.entity.LojaEntity
import pt.ipbeja.gestaocompras.data.repository.ComprasRepository

class LojasViewModel(
    private val repo: ComprasRepository
) : ViewModel() {

    val lojas: StateFlow<List<LojaEntity>> = repo.lojas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun adicionar(nome: String, morada: String?, aberturaHm: String?, fechoHm: String?) {
        val nomeLimpo = nome.trim()
        if (nomeLimpo.isBlank()) return
        viewModelScope.launch {
            repo.guardarLoja(
                LojaEntity(
                    nome = nomeLimpo,
                    morada = morada?.trim()?.ifBlank { null },
                    horarioAbertura = aberturaHm?.trim()?.ifBlank { null },
                    horarioFecho = fechoHm?.trim()?.ifBlank { null }
                )
            )
        }
    }

    fun apagar(loja: LojaEntity) {
        viewModelScope.launch { repo.apagarLoja(loja) }
    }

    fun marcarFavorita(loja: LojaEntity) {
        viewModelScope.launch { repo.marcarFavorita(loja.id) }
    }
}
