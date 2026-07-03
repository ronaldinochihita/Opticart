package pt.ipbeja.gestaocompras.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import pt.ipbeja.gestaocompras.OpticartApplication
import pt.ipbeja.gestaocompras.ui.executor.modo.ModoCompraViewModel
import pt.ipbeja.gestaocompras.ui.executor.resumo.ResumoFinalViewModel
import pt.ipbeja.gestaocompras.ui.organizador.edicao.EdicaoListaViewModel
import pt.ipbeja.gestaocompras.ui.organizador.historico.HistoricoViewModel
import pt.ipbeja.gestaocompras.ui.organizador.listas.ListasViewModel
import pt.ipbeja.gestaocompras.ui.organizador.lojas.LojasViewModel

/**
 * Factory única — passa o Repository (do Application) a cada ViewModel.
 * Evita depender de Hilt/Koin para um projeto pequeno e ainda assim
 * mantém os ViewModels testáveis (sem singletons escondidos).
 */
private fun CreationExtras.repo() =
    (this[APPLICATION_KEY] as OpticartApplication).repository

val OpticartVMFactory: ViewModelProvider.Factory = viewModelFactory {
    initializer { ListasViewModel(repo()) }
    initializer { LojasViewModel(repo()) }
    initializer { HistoricoViewModel(repo()) }
    initializer { EdicaoListaViewModel(repo()) }
    initializer { ModoCompraViewModel(repo()) }
    initializer { ResumoFinalViewModel(repo()) }
}

// Sentinela para poder usar viewModel(factory = OpticartVMFactory) em qualquer ecrã.
@Suppress("UnusedReceiverParameter")
inline fun <reified T : ViewModel> Any.opticartVM(): ViewModelProvider.Factory =
    OpticartVMFactory
