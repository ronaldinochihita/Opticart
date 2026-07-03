package pt.ipbeja.gestaocompras.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lojas")
data class LojaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val morada: String? = null,
    val horarioAbertura: String? = null,
    val horarioFecho: String? = null,
    val favorita: Boolean = false
)
