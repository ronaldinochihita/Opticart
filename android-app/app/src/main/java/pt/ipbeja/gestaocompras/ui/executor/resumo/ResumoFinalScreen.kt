package pt.ipbeja.gestaocompras.ui.executor.resumo

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.ipbeja.gestaocompras.ui.OpticartVMFactory
import pt.ipbeja.gestaocompras.ui.components.ItemAvatar
import pt.ipbeja.gestaocompras.ui.components.OpticartCard
import pt.ipbeja.gestaocompras.ui.theme.OpticartLaranja
import pt.ipbeja.gestaocompras.ui.theme.OpticartTextoSuave
import pt.ipbeja.gestaocompras.ui.theme.OpticartVerde

@Composable
fun ResumoFinalScreen(
    listaId: Long,
    onVoltar: () -> Unit,
    onFechar: () -> Unit,
    vm: ResumoFinalViewModel = viewModel(factory = OpticartVMFactory)
) {
    LaunchedEffect(listaId) { vm.carregar(listaId) }
    val estado by vm.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVoltar) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
            }
            Column {
                Text(
                    "Resumo Final",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    estado.lista?.nome ?: "…",
                    style = MaterialTheme.typography.titleMedium,
                    color = OpticartTextoSuave
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        CartaoTotais(
            totalReal = estado.totalReal,
            totalArredondado = estado.totalArredondado
        )

        // Aviso lista sem loja associada
        if (estado.lista != null && estado.loja == null) {
            Spacer(Modifier.height(12.dp))
            Aviso(
                texto = "Esta lista não tem loja atribuída. Os preços não vão ser gravados no histórico. Volta atrás e escolhe uma loja se quiseres registá-los."
            )
        }

        // Aviso itens sem preço
        if (estado.itensSemPreco.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Aviso(
                texto = "Há ${estado.itensSemPreco.size} item(s) sem preço registado — esses não vão aparecer no histórico de preços."
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "Itens comprados (${estado.itensCarrinho.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(estado.itensCarrinho, key = { it.id }) { item ->
                LinhaItemComprado(
                    descricao = item.descricao,
                    quantidade = item.quantidade.toInt(),
                    unidade = item.unidade ?: "un",
                    total = (item.precoUnitario ?: 0.0) * item.quantidade
                )
            }
        }

        Button(
            onClick = {
                vm.fechar { gravados ->
                    val msg = when {
                        gravados > 0 -> "Compra fechada. $gravados preço(s) gravados no histórico."
                        else -> "Compra fechada."
                    }
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    onFechar()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = OpticartVerde),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(bottom = 12.dp)
        ) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("FECHAR E GUARDAR PREÇOS", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// -------- Cartão de totais --------

@Composable
private fun CartaoTotais(totalReal: Double, totalArredondado: Double) {
    // Usa surfaceVariant do tema (verde-claro em light, verde-escuro em dark)
    // e onSurfaceVariant para o texto → contraste garantido nos dois modos.
    OpticartCard(highlighted = true) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    RotuloPequeno("Total real")
                    Text(
                        formatarEuros(totalReal),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    RotuloPequeno("A pagar (arredondado)")
                    Text(
                        formatarEuros(totalArredondado),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            val diferenca = totalArredondado - totalReal
            Text(
                "Arredondamento: ${formatarEuros(diferenca)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun RotuloPequeno(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
    )
}

// -------- Linha do item comprado (adaptativa ao tema) --------

@Composable
private fun LinhaItemComprado(
    descricao: String,
    quantidade: Int,
    unidade: String,
    total: Double
) {
    // primaryContainer + onPrimaryContainer garantem contraste em light/dark.
    val bg = MaterialTheme.colorScheme.primaryContainer
    val fg = MaterialTheme.colorScheme.onPrimaryContainer
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        ItemAvatar(nome = descricao, size = 40)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                descricao,
                fontWeight = FontWeight.SemiBold,
                color = fg
            )
            Text(
                "$quantidade $unidade",
                style = MaterialTheme.typography.bodySmall,
                color = fg.copy(alpha = 0.75f)
            )
        }
        Text(
            formatarEuros(total),
            fontWeight = FontWeight.Bold,
            color = fg
        )
    }
}

// -------- Aviso --------

@Composable
private fun Aviso(texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33FB8C00), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Icon(
            Icons.Filled.Warning,
            contentDescription = null,
            tint = OpticartLaranja
        )
        Spacer(Modifier.width(8.dp))
        Text(
            texto,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatarEuros(v: Double): String = "%.2f €".format(v).replace('.', ',')
