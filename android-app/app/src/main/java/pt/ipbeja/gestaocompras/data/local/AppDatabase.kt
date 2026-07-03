package pt.ipbeja.gestaocompras.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pt.ipbeja.gestaocompras.data.local.dao.HistoricoDao
import pt.ipbeja.gestaocompras.data.local.dao.HistoricoPrecoDao
import pt.ipbeja.gestaocompras.data.local.dao.ItemListaDao
import pt.ipbeja.gestaocompras.data.local.dao.ListaCompraDao
import pt.ipbeja.gestaocompras.data.local.dao.LojaDao
import pt.ipbeja.gestaocompras.data.local.entity.HistoricoCompraEntity
import pt.ipbeja.gestaocompras.data.local.entity.HistoricoPrecoEntity
import pt.ipbeja.gestaocompras.data.local.entity.ItemListaEntity
import pt.ipbeja.gestaocompras.data.local.entity.ListaCompraEntity
import pt.ipbeja.gestaocompras.data.local.entity.LojaEntity

@Database(
    entities = [
        LojaEntity::class,
        ListaCompraEntity::class,
        ItemListaEntity::class,
        HistoricoCompraEntity::class,
        HistoricoPrecoEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun lojaDao(): LojaDao
    abstract fun listaCompraDao(): ListaCompraDao
    abstract fun itemListaDao(): ItemListaDao
    abstract fun historicoDao(): HistoricoDao
    abstract fun historicoPrecoDao(): HistoricoPrecoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gestao_compras.db"
                )
                    // Trabalho académico — não temos utilizadores reais a proteger.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
