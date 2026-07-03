package pt.ipbeja.gestaocompras.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pt.ipbeja.gestaocompras.data.local.entity.LojaEntity

@Dao
interface LojaDao {

    // Favoritas primeiro, depois ordem alfabética.
    @Query("SELECT * FROM lojas ORDER BY favorita DESC, nome ASC")
    fun observarTodas(): Flow<List<LojaEntity>>

    @Query("SELECT * FROM lojas WHERE id = :id")
    suspend fun obterPorId(id: Long): LojaEntity?

    @Query("SELECT * FROM lojas WHERE favorita = 1 LIMIT 1")
    suspend fun obterFavorita(): LojaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(loja: LojaEntity): Long

    @Update
    suspend fun atualizar(loja: LojaEntity)

    @Delete
    suspend fun apagar(loja: LojaEntity)

    // Só uma loja pode ser favorita em cada momento — limpar tudo antes de marcar.
    @Query("UPDATE lojas SET favorita = 0")
    suspend fun limparFavoritas()

    @Query("UPDATE lojas SET favorita = 1 WHERE id = :id")
    suspend fun marcarFavoritaInterno(id: Long)
}
