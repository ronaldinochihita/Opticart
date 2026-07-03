package pt.ipbeja.gestaocompras.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Registo de um preço de um produto numa loja num dado momento.
 * Sem FK para lojaId — a integridade do histórico não deve depender
 * da existência da loja (a loja pode ser apagada mais tarde).
 */
@Entity(
    tableName = "historico_precos",
    indices = [Index("produtoNome"), Index("lojaId")]
)
data class HistoricoPrecoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val produtoNome: String,
    val lojaId: Long,
    val nomeLoja: String,
    val preco: Double,
    val data: Long = System.currentTimeMillis()
)
