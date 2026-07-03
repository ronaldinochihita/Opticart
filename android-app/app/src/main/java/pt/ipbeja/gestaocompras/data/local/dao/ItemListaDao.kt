package pt.ipbeja.gestaocompras.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pt.ipbeja.gestaocompras.data.local.entity.ItemListaEntity

@Dao
interface ItemListaDao {
    @Query("SELECT * FROM itens_lista WHERE listaId = :listaId ORDER BY descricao ASC")
    fun observarPorLista(listaId: Long): Flow<List<ItemListaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(item: ItemListaEntity): Long

    @Update
    suspend fun atualizar(item: ItemListaEntity)

    @Delete
    suspend fun apagar(item: ItemListaEntity)

    @Query("UPDATE itens_lista SET noCarrinho = :no WHERE id = :id")
    suspend fun marcarNoCarrinho(id: Long, no: Boolean)
}
