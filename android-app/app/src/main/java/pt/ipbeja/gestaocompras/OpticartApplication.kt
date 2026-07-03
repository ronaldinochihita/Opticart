package pt.ipbeja.gestaocompras

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pt.ipbeja.gestaocompras.data.local.AppDatabase
import pt.ipbeja.gestaocompras.data.repository.ComprasRepository
import pt.ipbeja.gestaocompras.data.seed.SeedData

class OpticartApplication : Application() {

    // Scope de aplicação — sobrevive ao ciclo de vida de Activities.
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database: AppDatabase by lazy { AppDatabase.get(this) }
    val repository: ComprasRepository by lazy { ComprasRepository(database) }

    override fun onCreate() {
        super.onCreate()
        // Semear a base de dados no primeiro arranque (idempotente).
        appScope.launch { SeedData.popularSeVazio(repository) }
    }
}
