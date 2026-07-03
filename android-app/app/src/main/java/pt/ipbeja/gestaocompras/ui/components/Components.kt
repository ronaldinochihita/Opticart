package pt.ipbeja.gestaocompras.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipbeja.gestaocompras.ui.theme.ItemAvatarPalette
import pt.ipbeja.gestaocompras.ui.theme.OpticartTextoSuave
import pt.ipbeja.gestaocompras.ui.theme.OpticartVerde
import pt.ipbeja.gestaocompras.ui.theme.OpticartVerdeChip

/**
 * Círculo verde claro com o pictograma de loja — igual em todos os ecrãs.
 */
@Composable
fun StoreIcon(size: Int = 48) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(OpticartVerdeChip, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Storefront,
            contentDescription = null,
            tint = OpticartVerde,
            modifier = Modifier.size((size * 0.55).dp)
        )
    }
}

/**
 * Quadrado colorido com uma letra maiúscula — usado para representar
 * cada produto no ecrã Modo Compra. A cor deriva do próprio nome
 * para ficar consistente entre ecrãs sem precisar de guardar na BD.
 */
@Composable
fun ItemAvatar(nome: String, size: Int = 48) {
    val cor = corParaNome(nome)
    val inicial = nome.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(cor, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = inicial,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size * 0.42).sp
        )
    }
}

fun corParaNome(nome: String): Color {
    // Soma dos code points → índice na paleta. Determinista e não requer estado.
    val soma = nome.sumOf { it.code }
    return ItemAvatarPalette[soma % ItemAvatarPalette.size]
}

/**
 * Cartão branco arredondado, com sombra suave — a "unidade visual" base
 * de todos os ecrãs, tal como aparece nos mocks.
 */
@Composable
fun OpticartCard(
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    val fundo = if (highlighted)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surface

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = fundo),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp),
        border = if (highlighted)
            androidx.compose.foundation.BorderStroke(1.dp, OpticartVerde)
        else null
    ) {
        Box(modifier = Modifier.padding(padding)) { content() }
    }
}

/**
 * Placeholder amigável quando uma lista está vazia.
 */
@Composable
fun EmptyState(
    titulo: String,
    subtitulo: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = OpticartTextoSuave
            )
            if (subtitulo != null) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OpticartTextoSuave
                )
            }
        }
    }
}

/**
 * Cabeçalho da marca (usado no ecrã Home).
 */
@Composable
fun OpticartBrand() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.ShoppingCart,
            contentDescription = null,
            tint = OpticartVerde,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "Opticart",
            style = MaterialTheme.typography.headlineMedium,
            color = OpticartVerde,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Marcador visual para as linhas de código em placeholder velho.
 * Mantido apenas para compatibilidade — a maioria dos ecrãs deixou
 * de usar isto.
 */
@Composable
fun PlaceholderScreen(titulo: String) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = titulo, style = MaterialTheme.typography.titleLarge)
        }
    }
}
