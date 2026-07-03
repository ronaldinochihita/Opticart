package pt.ipbeja.gestaocompras.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipbeja.gestaocompras.data.local.entity.HistoricoCompraEntity

@Dao
interface HistoricoDao {
    @Query("SELECT * FROM historico_compras ORDER BY concluidaEm DESC")
    fun observarTodas(): Flow<List<HistoricoCompraEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(registo: HistoricoCompraEntity): Long
}
