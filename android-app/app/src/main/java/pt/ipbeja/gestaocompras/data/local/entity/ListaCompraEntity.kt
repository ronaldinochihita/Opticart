package pt.ipbeja.gestaocompras.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "listas_compra",
    foreignKeys = [
        ForeignKey(
            entity = LojaEntity::class,
            parentColumns = ["id"],
            childColumns = ["lojaId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("lojaId")]
)
data class ListaCompraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val lojaId: Long? = null,
    val criadaEm: Long = System.currentTimeMillis(),
    val concluida: Boolean = false
)
