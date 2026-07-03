package pt.ipbeja.gestaocompras.data.repository

import kotlinx.coroutines.flow.Flow
import pt.ipbeja.gestaocompras.data.local.AppDatabase
import pt.ipbeja.gestaocompras.data.local.entity.HistoricoCompraEntity
import pt.ipbeja.gestaocompras.data.local.entity.HistoricoPrecoEntity
import pt.ipbeja.gestaocompras.data.local.entity.ItemListaEntity
import pt.ipbeja.gestaocompras.data.local.entity.ListaCompraEntity
import pt.ipbeja.gestaocompras.data.local.entity.LojaEntity

class ComprasRepository(private val db: AppDatabase) {

    private val lojaDao = db.lojaDao()
    private val listaDao = db.listaCompraDao()
    private val itemDao = db.itemListaDao()
    private val historicoDao = db.historicoDao()
    private val historicoPrecoDao = db.historicoPrecoDao()

    // ---- Lojas ----
    fun lojas(): Flow<List<LojaEntity>> = lojaDao.observarTodas()
    suspend fun obterLoja(id: Long): LojaEntity? = lojaDao.obterPorId(id)
    suspend fun obterLojaFavorita(): LojaEntity? = lojaDao.obterFavorita()
    suspend fun guardarLoja(loja: LojaEntity): Long = lojaDao.inserir(loja)
    suspend fun atualizarLoja(loja: LojaEntity) = lojaDao.atualizar(loja)
    suspend fun apagarLoja(loja: LojaEntity) = lojaDao.apagar(loja)
    suspend fun marcarFavorita(id: Long) {
        lojaDao.limparFavoritas()
        lojaDao.marcarFavoritaInterno(id)
    }

    // ---- Listas ----
    fun listasAtivas(): Flow<List<ListaCompraEntity>> = listaDao.observarAtivas()
    fun resumosDeListasAtivas() = listaDao.observarResumosAtivos()
    suspend fun obterLista(id: Long): ListaCompraEntity? = listaDao.obterPorId(id)
    suspend fun guardarLista(lista: ListaCompraEntity): Long = listaDao.inserir(lista)
    suspend fun atualizarLista(lista: ListaCompraEntity) = listaDao.atualizar(lista)
    suspend fun apagarLista(lista: ListaCompraEntity) = listaDao.apagar(lista)
    suspend fun concluirLista(id: Long) = listaDao.marcarConcluida(id)

    // ---- Itens ----
    fun itensDaLista(listaId: Long): Flow<List<ItemListaEntity>> =
        itemDao.observarPorLista(listaId)
    suspend fun guardarItem(item: ItemListaEntity): Long = itemDao.inserir(item)
    suspend fun atualizarItem(item: ItemListaEntity) = itemDao.atualizar(item)
    suspend fun apagarItem(item: ItemListaEntity) = itemDao.apagar(item)
    suspend fun marcarItemNoCarrinho(id: Long, no: Boolean) =
        itemDao.marcarNoCarrinho(id, no)

    // ---- Histórico de compras (fluxo do Executor) ----
    fun historicoCompras(): Flow<List<HistoricoCompraEntity>> = historicoDao.observarTodas()
    suspend fun registarHistoricoCompra(registo: HistoricoCompraEntity): Long =
        historicoDao.inserir(registo)

    // ---- Histórico de preços (ecrã de comparação) ----
    fun produtosComHistorico(): Flow<List<String>> = historicoPrecoDao.observarProdutos()
    fun precosPorProduto(produto: String): Flow<List<HistoricoPrecoEntity>> =
        historicoPrecoDao.observarPrecosPorProduto(produto)
    fun evolucaoPreco(produto: String, lojaId: Long): Flow<List<HistoricoPrecoEntity>> =
        historicoPrecoDao.observarEvolucao(produto, lojaId)
    suspend fun registarPreco(registo: HistoricoPrecoEntity): Long =
        historicoPrecoDao.inserir(registo)
    suspend fun registarPrecos(registos: List<HistoricoPrecoEntity>) =
        historicoPrecoDao.inserirVarios(registos)
}
