package pt.ipbeja.gestaocompras.ui.organizador.edicao

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.ipbeja.gestaocompras.data.local.entity.ItemListaEntity
import pt.ipbeja.gestaocompras.data.local.entity.LojaEntity
import pt.ipbeja.gestaocompras.ui.OpticartVMFactory
import pt.ipbeja.gestaocompras.ui.components.OpticartCard
import pt.ipbeja.gestaocompras.ui.components.StoreIcon
import pt.ipbeja.gestaocompras.ui.theme.OpticartTextoSuave
import pt.ipbeja.gestaocompras.ui.theme.OpticartVerde
import pt.ipbeja.gestaocompras.ui.theme.OpticartVerdeSuave

@Composable
fun EdicaoListaScreen(
    listaId: Long,
    onVoltar: () -> Unit,
    onIniciarModoCompra: () -> Unit,
    vm: EdicaoListaViewModel = viewModel(factory = OpticartVMFactory)
) {
    LaunchedEffect(listaId) { vm.carregar(listaId) }

    val estado by vm.estado.collectAsStateWithLifecycle()
    val lojas by vm.lojas.collectAsStateWithLifecycle()

    var mostrarNovoItem by remember { mutableStateOf(false) }
    var mostrarEscolhaLoja by remember { mutableStateOf(false) }
    var menuAberto by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        // Barra topo
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVoltar) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
            }
            Text(
                estado.lista?.nome ?: "…",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Box {
                IconButton(onClick = { menuAberto = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Opções")
                }
                DropdownMenu(expanded = menuAberto, onDismissRequest = { menuAberto = false }) {
                    DropdownMenuItem(
                        text = { Text("Alterar loja") },
                        onClick = { mostrarEscolhaLoja = true; menuAberto = false }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        // Cartão loja selecionada
        LojaSelecionada(
            loja = estado.loja,
            onClick = { mostrarEscolhaLoja = true }
        )

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Itens da lista (${estado.itens.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(8.dp))

        // Lista de itens
        OpticartCard(padding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)) {
            Column {
                if (estado.itens.isEmpty()) {
                    Text(
                        "Ainda sem itens — adiciona o primeiro em baixo.",
                        color = OpticartTextoSuave,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                estado.itens.forEach { item ->
                    LinhaItem(
                        item = item,
                        onToggle = { vm.alternarComprado(item) },
                        onApagar = { vm.apagarItem(item) }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            "+ Adicionar item",
            color = OpticartVerde,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { mostrarNovoItem = true }
                .padding(8.dp)
        )

        Spacer(Modifier.weight(1f))

        // Barra inferior com total + botão INICIAR MODO COMPRA
        BarraTotalEIniciar(
            total = estado.totalEstimado,
            onIniciar = onIniciarModoCompra,
            enabled = estado.itens.isNotEmpty()
        )
        Spacer(Modifier.height(12.dp))
    }

    if (mostrarNovoItem) {
        DialogoNovoItem(
            onConfirmar = { desc, qtd, unidade, preco ->
                vm.adicionarItem(desc, qtd, unidade, preco)
                mostrarNovoItem = false
            },
            onCancelar = { mostrarNovoItem = false }
        )
    }

    if (mostrarEscolhaLoja) {
        DialogoEscolherLoja(
            lojas = lojas,
            atual = estado.loja,
            onEscolher = {
                vm.escolherLoja(it)
                mostrarEscolhaLoja = false
            },
            onCancelar = { mostrarEscolhaLoja = false }
        )
    }
}

@Composable
private fun LojaSelecionada(loja: LojaEntity?, onClick: () -> Unit) {
    OpticartCard(highlighted = true, modifier = Modifier.clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StoreIcon(size = 40)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Loja selecionada",
                    style = MaterialTheme.typography.bodySmall,
                    color = OpticartTextoSuave
                )
                Text(
                    loja?.nome ?: "Escolher loja",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(Icons.Filled.Edit, contentDescription = null, tint = OpticartVerde)
        }
    }
}

@Composable
private fun LinhaItem(
    item: ItemListaEntity,
    onToggle: () -> Unit,
    onApagar: () -> Unit
) {
    var menuAberto by remember { mutableStateOf(false) }
    var pedirConfirmacao by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Checkbox(
            checked = item.noCarrinho,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = OpticartVerde,
                uncheckedColor = OpticartTextoSuave
            )
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.descricao,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (item.noCarrinho) TextDecoration.LineThrough else null,
                color = if (item.noCarrinho) OpticartTextoSuave else MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            "%.0f %s".format(item.quantidade, item.unidade ?: "un"),
            style = MaterialTheme.typography.bodySmall,
            color = OpticartTextoSuave,
            modifier = Modifier.width(56.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            formatarEuros((item.precoUnitario ?: 0.0) * item.quantidade),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Box {
            IconButton(onClick = { menuAberto = true }) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = "Opções",
                    tint = OpticartTextoSuave,
                    modifier = Modifier.size(18.dp)
                )
            }
            DropdownMenu(expanded = menuAberto, onDismissRequest = { menuAberto = false }) {
                DropdownMenuItem(
                    text = { Text("Eliminar item") },
                    onClick = { menuAberto = false; pedirConfirmacao = true }
                )
            }
        }
    }

    if (pedirConfirmacao) {
        AlertDialog(
            onDismissRequest = { pedirConfirmacao = false },
            title = { Text("Eliminar item?") },
            text = { Text("Vais eliminar “${item.descricao}” desta lista.") },
            confirmButton = {
                TextButton(onClick = { pedirConfirmacao = false; onApagar() }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { pedirConfirmacao = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun BarraTotalEIniciar(
    total: Double,
    onIniciar: () -> Unit,
    enabled: Boolean
) {
    OpticartCard {
        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Total estimado",
                        style = MaterialTheme.typography.bodySmall,
                        color = OpticartTextoSuave
                    )
                    Text(
                        formatarEuros(total),
                        style = MaterialTheme.typography.headlineMedium,
                        color = OpticartVerde,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onIniciar,
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(containerColor = OpticartVerde),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("INICIAR MODO COMPRA", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ------------- Diálogos -------------

@Composable
private fun DialogoNovoItem(
    onConfirmar: (String, Double, String?, Double?) -> Unit,
    onCancelar: () -> Unit
) {
    var desc by remember { mutableStateOf("") }
    var qtd by remember { mutableStateOf("1") }
    var unidade by remember { mutableStateOf("un") }
    var preco by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Novo item") },
        text = {
            Column {
                OutlinedTextField(
                    value = desc, onValueChange = { desc = it },
                    label = { Text("Nome*") }, singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    OutlinedTextField(
                        value = qtd, onValueChange = { qtd = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Qtd") }, singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = unidade, onValueChange = { unidade = it },
                        label = { Text("Un") }, singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = preco, onValueChange = { preco = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                        label = { Text("Preço €") }, singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirmar(
                    desc,
                    qtd.toDoubleOrNull() ?: 1.0,
                    unidade,
                    preco.replace(',', '.').toDoubleOrNull()
                )
            }) { Text("Adicionar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@Composable
private fun DialogoEscolherLoja(
    lojas: List<LojaEntity>,
    atual: LojaEntity?,
    onEscolher: (LojaEntity?) -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Loja para esta lista") },
        text = {
            LazyColumn {
                item {
                    LinhaOpcao(
                        rotulo = "Sem loja atribuída",
                        selecionado = atual == null,
                        onClick = { onEscolher(null) }
                    )
                }
                items(lojas, key = { it.id }) { loja ->
                    LinhaOpcao(
                        rotulo = loja.nome,
                        selecionado = atual?.id == loja.id,
                        onClick = { onEscolher(loja) }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onCancelar) { Text("Fechar") } }
    )
}

@Composable
private fun LinhaOpcao(rotulo: String, selecionado: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selecionado) OpticartVerdeSuave else androidx.compose.ui.graphics.Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 12.dp)
    ) {
        Text(
            rotulo,
            fontWeight = if (selecionado) FontWeight.Bold else FontWeight.Normal,
            color = if (selecionado) OpticartVerde else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatarEuros(v: Double): String = "%.2f €".format(v).replace('.', ',')
