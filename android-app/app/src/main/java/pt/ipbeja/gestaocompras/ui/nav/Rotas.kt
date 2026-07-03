package pt.ipbeja.gestaocompras.ui.nav

/**
 * Rotas da app. Objectos com string base + helpers para as rotas
 * com argumentos, para evitar magic strings espalhadas pelo código.
 */
object Rotas {
    // Top-level (tabs no fundo)
    const val LISTAS = "listas"
    const val HISTORICO = "historico"
    const val LOJAS = "lojas"

    // Detalhes (ecrã cheio, sem bottom bar)
    const val EDICAO = "edicao/{listaId}"
    const val MODO = "modo/{listaId}"
    const val RESUMO = "resumo/{listaId}"

    fun edicao(listaId: Long) = "edicao/$listaId"
    fun modo(listaId: Long) = "modo/$listaId"
    fun resumo(listaId: Long) = "resumo/$listaId"

    val rotasComBottomBar = setOf(LISTAS, HISTORICO, LOJAS)
}
