package pt.ipbeja.gestaocompras.ui.organizador.historico

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.ipbeja.gestaocompras.data.local.entity.HistoricoPrecoEntity
import pt.ipbeja.gestaocompras.ui.OpticartVMFactory
import pt.ipbeja.gestaocompras.ui.components.EmptyState
import pt.ipbeja.gestaocompras.ui.components.OpticartCard
import pt.ipbeja.gestaocompras.ui.theme.OpticartTextoSuave
import pt.ipbeja.gestaocompras.ui.theme.OpticartVerde
import pt.ipbeja.gestaocompras.ui.theme.OpticartVerdeSuave
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

@Composable
fun HistoricoScreen(
    vm: HistoricoViewModel = viewModel(factory = OpticartVMFactory)
) {
    val produtos by vm.produtos.collectAsStateWithLifecycle()
    val selecionado by vm.produtoSelecionado.collectAsStateWithLifecycle()
    val comparacao by vm.comparacao.collectAsStateWithLifecycle()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            "Histórico de Preços",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))

        if (produtos.isEmpty()) {
            EmptyState(
                titulo = "Sem histórico ainda",
                subtitulo = "Depois de compras registadas, aparece aqui a evolução dos preços."
            )
            return
        }

        DropdownProduto(
            produtos = produtos,
            selecionado = selecionado,
            onSelecionar = vm::selecionar
        )

        Spacer(Modifier.height(12.dp))
        CartaoComparacao(comparacao)

        Spacer(Modifier.height(12.dp))
        CartaoEvolucao(comparacao)
    }
}

@Composable
private fun DropdownProduto(
    produtos: List<String>,
    selecionado: String?,
    onSelecionar: (String) -> Unit
) {
    var aberto by remember { mutableStateOf(false) }
    OpticartCard(modifier = Modifier.clickable { aberto = true }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Produto",
                    style = MaterialTheme.typography.bodySmall,
                    color = OpticartTextoSuave
                )
                Text(
                    selecionado ?: "—",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = OpticartVerde
            )
        }
        DropdownMenu(
            expanded = aberto,
            onDismissRequest = { aberto = false }
        ) {
            produtos.forEach { produto ->
                DropdownMenuItem(
                    text = { Text(produto) },
                    onClick = { onSelecionar(produto); aberto = false }
                )
            }
        }
    }
}

@Composable
private fun CartaoComparacao(registos: List<HistoricoPrecoEntity>) {
    // Manter só o registo mais recente de cada loja; ordenar por preço.
    val comparacao = remember(registos) {
        registos.groupBy { it.lojaId }
            .map { (_, valores) -> valores.maxBy { it.data } }
            .sortedBy { it.preco }
    }
    val maisBarata = comparacao.firstOrNull()

    OpticartCard {
        Column {
            Text(
                "Comparação entre lojas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = OpticartVerde,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    "loja mais económica para este produto",
                    style = MaterialTheme.typography.bodySmall,
                    color = OpticartTextoSuave
                )
            }
            Spacer(Modifier.height(10.dp))
            comparacao.forEach { registo ->
                LinhaComparacao(
                    registo = registo,
                    destacar = registo === maisBarata
                )
            }
            if (comparacao.isEmpty()) {
                Text(
                    "Sem dados para o produto selecionado.",
                    color = OpticartTextoSuave,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun LinhaComparacao(registo: HistoricoPrecoEntity, destacar: Boolean) {
    val fundo = if (destacar) OpticartVerdeSuave else Color.Transparent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(fundo, RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp)
    ) {
        if (destacar) {
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                tint = OpticartVerde,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.size(6.dp))
        } else {
            Spacer(Modifier.size(22.dp))
        }
        Text(
            registo.nomeLoja,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (destacar) FontWeight.Bold else FontWeight.Normal,
            color = if (destacar) OpticartVerde else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            formatarEuros(registo.preco),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (destacar) OpticartVerde else MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.size(12.dp))
        Text(
            tempoRelativo(registo.data),
            style = MaterialTheme.typography.bodySmall,
            color = OpticartTextoSuave
        )
    }
}

@Composable
private fun CartaoEvolucao(registos: List<HistoricoPrecoEntity>) {
    // Gráfico da série completa (ordem cronológica).
    val serie = remember(registos) { registos.sortedBy { it.data } }
    if (serie.size < 2) return

    OpticartCard {
        Column {
            Text(
                "Evolução do preço",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "€/unidade · ${serie.size} registos",
                style = MaterialTheme.typography.bodySmall,
                color = OpticartTextoSuave
            )
            Spacer(Modifier.height(12.dp))
            GraficoLinha(
                serie = serie,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }
    }
}

/** Gráfico de linha simples desenhado em Canvas. */
@Composable
private fun GraficoLinha(
    serie: List<HistoricoPrecoEntity>,
    modifier: Modifier = Modifier
) {
    val minPreco = serie.minOf { it.preco }
    val maxPreco = serie.maxOf { it.preco }
    val amplitude = max(0.01, maxPreco - minPreco)

    Canvas(modifier = modifier) {
        val paddingH = 16f
        val paddingV = 24f
        val w = size.width - paddingH * 2
        val h = size.height - paddingV * 2

        val pontos = serie.mapIndexed { idx, r ->
            val x = paddingH + (idx.toFloat() / (serie.size - 1)) * w
            val y = paddingV + (1f - ((r.preco - minPreco) / amplitude).toFloat()) * h
            Offset(x, y)
        }

        // Área sob a linha (verde claro translúcido).
        val areaPath = Path().apply {
            moveTo(pontos.first().x, paddingV + h)
            pontos.forEach { lineTo(it.x, it.y) }
            lineTo(pontos.last().x, paddingV + h)
            close()
        }
        drawPath(areaPath, color = OpticartVerdeSuave)

        // Linha do gráfico.
        val linhaPath = Path().apply {
            moveTo(pontos.first().x, pontos.first().y)
            pontos.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(linhaPath, color = OpticartVerde, style = Stroke(width = 4f))

        // Pontos.
        pontos.forEach {
            drawCircle(color = OpticartVerde, radius = 6f, center = it)
            drawCircle(color = Color.White, radius = 3f, center = it)
        }
    }
}

// ---------- Utilitários ----------
private fun formatarEuros(v: Double): String = "%.2f €".format(v).replace('.', ',')

private fun tempoRelativo(millis: Long): String {
    val dias = ((System.currentTimeMillis() - millis) / (1000L * 60 * 60 * 24)).toInt()
    return when {
        dias < 1 -> "hoje"
        dias < 7 -> "há $dias dias"
        dias < 30 -> "há ${dias / 7} semanas"
        dias < 365 -> "há ${dias / 30} meses"
        else -> SimpleDateFormat("MMM yyyy", Locale("pt", "PT")).format(Date(millis))
    }
}
