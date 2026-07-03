package pt.ipbeja.gestaocompras.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipbeja.gestaocompras.data.local.entity.HistoricoPrecoEntity

@Dao
interface HistoricoPrecoDao {

    @Query("SELECT DISTINCT produtoNome FROM historico_precos ORDER BY produtoNome ASC")
    fun observarProdutos(): Flow<List<String>>

    // Preços mais recentes por loja para um produto (usado no cartão de comparação).
    @Query(
        """
        SELECT hp.* FROM historico_precos hp
        INNER JOIN (
            SELECT lojaId, MAX(data) AS ultima
            FROM historico_precos
            WHERE produtoNome = :produto
            GROUP BY lojaId
        ) mais_recente ON hp.lojaId = mais_recente.lojaId
                       AND hp.data = mais_recente.ultima
        WHERE hp.produtoNome = :produto
        ORDER BY hp.preco ASC
        """
    )
    fun observarPrecosPorProduto(produto: String): Flow<List<HistoricoPrecoEntity>>

    // Série temporal de um produto numa loja (para o gráfico de evolução).
    @Query(
        """
        SELECT * FROM historico_precos
        WHERE produtoNome = :produto AND lojaId = :lojaId
        ORDER BY data ASC
        """
    )
    fun observarEvolucao(produto: String, lojaId: Long): Flow<List<HistoricoPrecoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(registo: HistoricoPrecoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirVarios(registos: List<HistoricoPrecoEntity>)
}
