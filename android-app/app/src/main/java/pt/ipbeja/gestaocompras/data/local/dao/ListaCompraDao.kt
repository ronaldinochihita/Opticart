package pt.ipbeja.gestaocompras.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pt.ipbeja.gestaocompras.data.local.entity.ListaCompraEntity

/**
 * Linha "gorda" usada no ecrã Listas — traz cada lista já com a
 * contagem de itens e o total estimado numa só query.
 */
data class ListaResumoRow(
    val id: Long,
    val nome: String,
    val lojaId: Long?,
    val criadaEm: Long,
    val concluida: Boolean,
    val itens: Int,
    val totalEstimado: Double
)

@Dao
interface ListaCompraDao {

    @Query("SELECT * FROM listas_compra WHERE concluida = 0 ORDER BY criadaEm DESC")
    fun observarAtivas(): Flow<List<ListaCompraEntity>>

    @Query(
        """
        SELECT l.id, l.nome, l.lojaId, l.criadaEm, l.concluida,
               (SELECT COUNT(*) FROM itens_lista i WHERE i.listaId = l.id) AS itens,
               COALESCE(
                  (SELECT SUM(COALESCE(i.precoUnitario, 0) * i.quantidade)
                   FROM itens_lista i WHERE i.listaId = l.id),
                  0.0
               ) AS totalEstimado
        FROM listas_compra l
        WHERE l.concluida = 0
        ORDER BY l.criadaEm DESC
        """
    )
    fun observarResumosAtivos(): Flow<List<ListaResumoRow>>

    @Query("SELECT * FROM listas_compra WHERE id = :id")
    suspend fun obterPorId(id: Long): ListaCompraEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(lista: ListaCompraEntity): Long

    @Update
    suspend fun atualizar(lista: ListaCompraEntity)

    @Delete
    suspend fun apagar(lista: ListaCompraEntity)

    @Query("UPDATE listas_compra SET concluida = 1 WHERE id = :id")
    suspend fun marcarConcluida(id: Long)
}
