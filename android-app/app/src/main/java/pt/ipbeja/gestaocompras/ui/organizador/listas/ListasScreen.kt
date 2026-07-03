package pt.ipbeja.gestaocompras.ui.organizador.listas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.ipbeja.gestaocompras.ui.OpticartVMFactory
import pt.ipbeja.gestaocompras.ui.components.EmptyState
import pt.ipbeja.gestaocompras.ui.components.OpticartBrand
import pt.ipbeja.gestaocompras.ui.components.OpticartCard
import pt.ipbeja.gestaocompras.ui.components.StoreIcon
import pt.ipbeja.gestaocompras.ui.theme.OpticartTextoSuave
import pt.ipbeja.gestaocompras.ui.theme.OpticartVerde

@Composable
fun ListasScreen(
    onAbrirLista: (Long) -> Unit,
    vm: ListasViewModel = viewModel(factory = OpticartVMFactory)
) {
    val listas by vm.listas.collectAsStateWithLifecycle()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogo = true },
                containerColor = OpticartVerde,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nova lista")
            }
        }
    ) { inner ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(inner)
            .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OpticartBrand()
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "As minhas listas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            if (listas.isEmpty()) {
                EmptyState(
                    titulo = "Ainda não tens listas",
                    subtitulo = "Toca em + para criar a primeira"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(listas, key = { it.id }) { lista ->
                        CartaoLista(
                            lista = lista,
                            onClick = { onAbrirLista(lista.id) }
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        DialogoNovaLista(
            onConfirmar = { nome ->
                vm.criarLista(nome)
                mostrarDialogo = false
            },
            onCancelar = { mostrarDialogo = false }
        )
    }
}

@Composable
private fun CartaoLista(lista: ListaExibicao, onClick: () -> Unit) {
    OpticartCard(modifier = Modifier.clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StoreIcon(size = 44)
            Spacer(Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    lista.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    if (lista.itens == 1) "1 item" else "${lista.itens} itens",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OpticartTextoSuave
                )
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = OpticartVerde,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(
                        lista.nomeLoja ?: "Sem loja atribuída",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (lista.nomeLoja == null) OpticartTextoSuave else OpticartVerde
                    )
                }
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = OpticartTextoSuave
            )
        }
    }
}

@Composable
private fun DialogoNovaLista(
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nova lista") },
        text = {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome da lista") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirmar(nome) }) { Text("Criar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
