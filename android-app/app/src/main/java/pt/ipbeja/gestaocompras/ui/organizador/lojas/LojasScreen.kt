package pt.ipbeja.gestaocompras.ui.organizador.lojas

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import pt.ipbeja.gestaocompras.data.local.entity.LojaEntity
import pt.ipbeja.gestaocompras.ui.OpticartVMFactory
import pt.ipbeja.gestaocompras.ui.components.EmptyState
import pt.ipbeja.gestaocompras.ui.components.OpticartCard
import pt.ipbeja.gestaocompras.ui.components.StoreIcon
import pt.ipbeja.gestaocompras.ui.theme.OpticartTextoSuave
import pt.ipbeja.gestaocompras.ui.theme.OpticartVerde

@Composable
fun LojasScreen(
    vm: LojasViewModel = viewModel(factory = OpticartVMFactory)
) {
    val lojas by vm.lojas.collectAsStateWithLifecycle()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Lojas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar loja", tint = OpticartVerde)
            }
        }
        Text(
            "As minhas lojas (${lojas.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))

        if (lojas.isEmpty()) {
            EmptyState(
                titulo = "Sem lojas registadas",
                subtitulo = "Adiciona a primeira em cima à direita"
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(lojas, key = { it.id }) { loja ->
                    CartaoLoja(
                        loja = loja,
                        onFavoritar = { vm.marcarFavorita(loja) },
                        onApagar = { vm.apagar(loja) }
                    )
                }
            }
        }
    }

    if (mostrarDialogo) {
        DialogoNovaLoja(
            onConfirmar = { nome, morada, abertura, fecho ->
                vm.adicionar(nome, morada, abertura, fecho)
                mostrarDialogo = false
            },
            onCancelar = { mostrarDialogo = false }
        )
    }
}

@Composable
private fun CartaoLoja(
    loja: LojaEntity,
    onFavoritar: () -> Unit,
    onApagar: () -> Unit
) {
    OpticartCard(highlighted = loja.favorita) {
        Row(verticalAlignment = Alignment.Top) {
            if (loja.favorita) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "Favorita",
                    tint = OpticartVerde,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
            }
            StoreIcon(size = 44)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    loja.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (!loja.morada.isNullOrBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = OpticartVerde,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            loja.morada,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OpticartTextoSuave
                        )
                    }
                }
                if (!loja.horarioAbertura.isNullOrBlank() && !loja.horarioFecho.isNullOrBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.AccessTime,
                            contentDescription = null,
                            tint = OpticartVerde,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${loja.horarioAbertura} - ${loja.horarioFecho}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OpticartTextoSuave
                        )
                    }
                }
            }
            MenuLoja(onFavoritar = onFavoritar, onApagar = onApagar)
        }
    }
}

@Composable
private fun MenuLoja(onFavoritar: () -> Unit, onApagar: () -> Unit) {
    var aberto by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { aberto = true }) {
            Icon(Icons.Filled.MoreVert, contentDescription = "Opções", tint = OpticartTextoSuave)
        }
        DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            DropdownMenuItem(
                text = { Text("Marcar como favorita") },
                onClick = { onFavoritar(); aberto = false }
            )
            DropdownMenuItem(
                text = { Text("Eliminar") },
                onClick = { onApagar(); aberto = false }
            )
        }
    }
}

@Composable
private fun DialogoNovaLoja(
    onConfirmar: (String, String?, String?, String?) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var morada by remember { mutableStateOf("") }
    var abertura by remember { mutableStateOf("") }
    var fecho by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nova loja") },
        text = {
            Column {
                OutlinedTextField(
                    value = nome, onValueChange = { nome = it },
                    label = { Text("Nome*") }, singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = morada, onValueChange = { morada = it },
                    label = { Text("Morada") }, singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    OutlinedTextField(
                        value = abertura, onValueChange = { abertura = it },
                        label = { Text("Abre") }, singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = fecho, onValueChange = { fecho = it },
                        label = { Text("Fecha") }, singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmar(nome, morada, abertura, fecho) }) {
                Text("Adicionar")
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
