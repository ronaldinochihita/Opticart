package pt.ipbeja.gestaocompras.ui.executor.modo

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.ipbeja.gestaocompras.data.local.entity.ItemListaEntity
import pt.ipbeja.gestaocompras.ui.OpticartVMFactory
import pt.ipbeja.gestaocompras.ui.components.ItemAvatar
import pt.ipbeja.gestaocompras.ui.theme.OpticartTextoSuave
import pt.ipbeja.gestaocompras.ui.theme.OpticartVerde
import pt.ipbeja.gestaocompras.ui.theme.OpticartVerdeSuave

@Composable
fun ModoCompraScreen(
    listaId: Long,
    onVoltar: () -> Unit,
    onVerResumo: () -> Unit,
    vm: ModoCompraViewModel = viewModel(factory = OpticartVMFactory)
) {
    LaunchedEffect(listaId) { vm.carregar(listaId) }
    val estado by vm.estado.collectAsStateWithLifecycle()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        CartaoTotal(total = estado.totalArredondado)

        Spacer(Modifier.height(12.dp))
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
            Text(
                "SAIR MODO COMPRA",
                color = OpticartVerde,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.clickable {
                    vm.sair()
                    onVoltar()
                }
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            modifier = Modifier.weight(1f)
        ) {
            item {
                Text(
                    "POR ADICIONAR (${estado.porAdicionar.size})",
                    color = OpticartVerde,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            items(estado.porAdicionar, key = { it.id }) { item ->
                LinhaItem(
                    item = item,
                    noCarrinho = false,
                    onClick = { vm.marcar(item, true) }
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
                Text(
                    "NO CARRINHO (${estado.noCarrinho.size})",
                    color = OpticartTextoSuave,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            items(estado.noCarrinho, key = { it.id }) { item ->
                LinhaItem(
                    item = item,
                    noCarrinho = true,
                    onClick = { vm.marcar(item, false) }
                )
            }
        }

        // Botão fixo em baixo: Ver Resumo Final
        Button(
            onClick = onVerResumo,
            colors = ButtonDefaults.buttonColors(containerColor = OpticartVerde),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(bottom = 12.dp),
            enabled = estado.noCarrinho.isNotEmpty()
        ) {
            Text(
                "VER RESUMO FINAL ›",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun CartaoTotal(total: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = OpticartVerde),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "TOTAL ATUAL",
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        formatarEuros(total),
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "arredondado a 5 cêntimos",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 50.dp)
            )
        }
    }
}

@Composable
private fun LinhaItem(
    item: ItemListaEntity,
    noCarrinho: Boolean,
    onClick: () -> Unit
) {
    val fundo = if (noCarrinho) OpticartVerdeSuave else MaterialTheme.colorScheme.surface
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(fundo, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        ItemAvatar(nome = item.descricao, size = 48)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.descricao,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${item.quantidade.toInt()} ${item.unidade ?: "un"} • ${formatarEuros(item.precoUnitario ?: 0.0)}",
                style = MaterialTheme.typography.bodySmall,
                color = OpticartTextoSuave
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(if (noCarrinho) Color.Transparent else OpticartVerde, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (noCarrinho) {
                Box(
                    Modifier
                        .size(40.dp)
                        .background(Color.Transparent)
                        .padding(2.dp)
                        .background(
                            Color.White,
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "No carrinho",
                        tint = OpticartVerde,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Adicionar ao carrinho",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

private fun formatarEuros(v: Double): String = "%.2f €".format(v).replace('.', ',')
