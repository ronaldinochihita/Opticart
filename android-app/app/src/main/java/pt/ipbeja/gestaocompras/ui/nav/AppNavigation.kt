package pt.ipbeja.gestaocompras.ui.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pt.ipbeja.gestaocompras.ui.executor.modo.ModoCompraScreen
import pt.ipbeja.gestaocompras.ui.executor.resumo.ResumoFinalScreen
import pt.ipbeja.gestaocompras.ui.organizador.edicao.EdicaoListaScreen
import pt.ipbeja.gestaocompras.ui.organizador.historico.HistoricoScreen
import pt.ipbeja.gestaocompras.ui.organizador.listas.ListasScreen
import pt.ipbeja.gestaocompras.ui.organizador.lojas.LojasScreen

private data class TabItem(
    val rota: String,
    val label: String,
    val icone: ImageVector
)

private val tabs = listOf(
    TabItem(Rotas.LISTAS, "Listas", Icons.Filled.List),
    TabItem(Rotas.HISTORICO, "Histórico", Icons.Filled.BarChart),
    TabItem(Rotas.LOJAS, "Lojas", Icons.Filled.Storefront),
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val rotaAtual = backStack?.destination?.route

    Scaffold(
        bottomBar = {
            if (rotaAtual in Rotas.rotasComBottomBar) {
                BottomBar(navController = navController, rotaAtual = rotaAtual)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Grafo(navController = navController, contentPadding = padding)
    }
}

@Composable
private fun BottomBar(navController: NavHostController, rotaAtual: String?) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        tabs.forEach { tab ->
            val selected = rotaAtual == tab.rota
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(tab.rota) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(tab.icone, contentDescription = tab.label) },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
private fun Grafo(
    navController: NavHostController,
    contentPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Rotas.LISTAS,
        modifier = Modifier.padding(contentPadding)
    ) {
        composable(Rotas.LISTAS) {
            ListasScreen(
                onAbrirLista = { id -> navController.navigate(Rotas.edicao(id)) }
            )
        }
        composable(Rotas.HISTORICO) {
            HistoricoScreen()
        }
        composable(Rotas.LOJAS) {
            LojasScreen()
        }
        composable(
            route = Rotas.EDICAO,
            arguments = listOf(navArgument("listaId") { type = NavType.LongType })
        ) { entry ->
            val listaId = entry.arguments?.getLong("listaId") ?: 0L
            EdicaoListaScreen(
                listaId = listaId,
                onVoltar = { navController.popBackStack() },
                onIniciarModoCompra = {
                    navController.navigate(Rotas.modo(listaId))
                }
            )
        }
        composable(
            route = Rotas.MODO,
            arguments = listOf(navArgument("listaId") { type = NavType.LongType })
        ) { entry ->
            val listaId = entry.arguments?.getLong("listaId") ?: 0L
            ModoCompraScreen(
                listaId = listaId,
                onVoltar = { navController.popBackStack() },
                onVerResumo = { navController.navigate(Rotas.resumo(listaId)) }
            )
        }
        composable(
            route = Rotas.RESUMO,
            arguments = listOf(navArgument("listaId") { type = NavType.LongType })
        ) { entry ->
            val listaId = entry.arguments?.getLong("listaId") ?: 0L
            ResumoFinalScreen(
                listaId = listaId,
                onVoltar = { navController.popBackStack() },
                onFechar = {
                    // Voltar até ao topo (Listas) após fechar a compra.
                    navController.popBackStack(Rotas.LISTAS, inclusive = false)
                }
            )
        }
    }
}
