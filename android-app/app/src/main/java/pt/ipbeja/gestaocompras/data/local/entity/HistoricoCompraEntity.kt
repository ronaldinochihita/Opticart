package pt.ipbeja.gestaocompras.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historico_compras")
data class HistoricoCompraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listaId: Long,
    val nomeLista: String,
    val nomeLoja: String?,
    val totalEstimado: Double,
    val totalPago: Double,
    val arredondamento: Double,
    val concluidaEm: Long = System.currentTimeMillis()
)
