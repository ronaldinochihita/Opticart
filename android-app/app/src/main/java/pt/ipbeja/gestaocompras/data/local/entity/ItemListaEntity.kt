package pt.ipbeja.gestaocompras.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "itens_lista",
    foreignKeys = [
        ForeignKey(
            entity = ListaCompraEntity::class,
            parentColumns = ["id"],
            childColumns = ["listaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("listaId")]
)
data class ItemListaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listaId: Long,
    val descricao: String,
    val quantidade: Double = 1.0,
    val unidade: String? = null,
    val precoUnitario: Double? = null,
    val noCarrinho: Boolean = false
)
