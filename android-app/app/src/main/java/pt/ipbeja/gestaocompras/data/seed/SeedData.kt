package pt.ipbeja.gestaocompras.data.seed

import kotlinx.coroutines.flow.first
import pt.ipbeja.gestaocompras.data.local.entity.HistoricoPrecoEntity
import pt.ipbeja.gestaocompras.data.local.entity.ItemListaEntity
import pt.ipbeja.gestaocompras.data.local.entity.ListaCompraEntity
import pt.ipbeja.gestaocompras.data.local.entity.LojaEntity
import pt.ipbeja.gestaocompras.data.repository.ComprasRepository

/**
 * Popula a base de dados com dados de exemplo alinhados com os
 * ecrãs desenhados no trabalho de SI (Opticart) — apenas se a BD
 * estiver vazia. Assim a demo abre sempre com conteúdo realista.
 */
object SeedData {

    suspend fun popularSeVazio(repo: ComprasRepository) {
        val jaTemLojas = repo.lojas().first().isNotEmpty()
        if (jaTemLojas) return

        // ------- LOJAS -------
        val minipreco = repo.guardarLoja(
            LojaEntity(
                nome = "Minipreço V. da Gama",
                morada = "Av. Vasco da Gama, Beja",
                horarioAbertura = "08h",
                horarioFecho = "22h",
                favorita = true
            )
        )
        val pingoDoce = repo.guardarLoja(
            LojaEntity(
                nome = "Pingo Doce Beja",
                morada = "Rua de Mértola, Beja",
                horarioAbertura = "08h",
                horarioFecho = "22h"
            )
        )
        val continente = repo.guardarLoja(
            LojaEntity(
                nome = "Continente Retail Park",
                morada = "Retail Park, Beja",
                horarioAbertura = "09h",
                horarioFecho = "23h"
            )
        )
        val lidl = repo.guardarLoja(
            LojaEntity(
                nome = "Lidl Beja",
                morada = "Estrada de Mértola, Beja",
                horarioAbertura = "08h",
                horarioFecho = "21h"
            )
        )

        // Loja fantasma "Leroy Merlin" para a lista Bricolage (não é supermercado
        // mas serve para mostrar que o app suporta qualquer tipo de estabelecimento).
        val leroy = repo.guardarLoja(
            LojaEntity(
                nome = "Leroy Merlin",
                morada = "Loulé Retail Park",
                horarioAbertura = "09h",
                horarioFecho = "22h"
            )
        )

        // ------- LISTAS + ITENS (alinhado com o mockup do ecrã 2) -------
        val comprasSemanais = repo.guardarLista(
            ListaCompraEntity(nome = "Compras Semanais", lojaId = minipreco)
        )

        listOf(
            ItemListaEntity(listaId = comprasSemanais, descricao = "Leite 1L", quantidade = 1.0, unidade = "un", precoUnitario = 1.20),
            ItemListaEntity(listaId = comprasSemanais, descricao = "Pão", quantidade = 2.0, unidade = "un", precoUnitario = 1.20),
            ItemListaEntity(listaId = comprasSemanais, descricao = "Azeite 500ml", quantidade = 1.0, unidade = "un", precoUnitario = 5.99),
            ItemListaEntity(listaId = comprasSemanais, descricao = "Maçãs", quantidade = 1.0, unidade = "kg", precoUnitario = 2.30),
            ItemListaEntity(listaId = comprasSemanais, descricao = "Atum (3 por 2)", quantidade = 3.0, unidade = "un", precoUnitario = 0.97),
            ItemListaEntity(listaId = comprasSemanais, descricao = "Massa Espiral", quantidade = 1.0, unidade = "un", precoUnitario = 0.85),
            ItemListaEntity(listaId = comprasSemanais, descricao = "Iogurtes naturais", quantidade = 6.0, unidade = "un", precoUnitario = 0.30),
            ItemListaEntity(listaId = comprasSemanais, descricao = "Café", quantidade = 1.0, unidade = "un", precoUnitario = 3.40),
            ItemListaEntity(listaId = comprasSemanais, descricao = "Manteiga", quantidade = 1.0, unidade = "un", precoUnitario = 2.15),
            ItemListaEntity(listaId = comprasSemanais, descricao = "Ovos M6", quantidade = 1.0, unidade = "cx", precoUnitario = 1.99),
            ItemListaEntity(listaId = comprasSemanais, descricao = "Fiambre", quantidade = 1.0, unidade = "un", precoUnitario = 1.79),
            ItemListaEntity(listaId = comprasSemanais, descricao = "Queijo Flamengo", quantidade = 1.0, unidade = "un", precoUnitario = 3.29),
        ).forEach { repo.guardarItem(it) }

        val festa = repo.guardarLista(
            ListaCompraEntity(nome = "Festa Aniversário", lojaId = null)
        )
        listOf(
            ItemListaEntity(listaId = festa, descricao = "Bolo de aniversário", quantidade = 1.0, unidade = "un", precoUnitario = 18.50),
            ItemListaEntity(listaId = festa, descricao = "Salgadinhos", quantidade = 30.0, unidade = "un", precoUnitario = 0.40),
            ItemListaEntity(listaId = festa, descricao = "Refrigerantes", quantidade = 6.0, unidade = "un", precoUnitario = 1.20),
            ItemListaEntity(listaId = festa, descricao = "Água 1,5L", quantidade = 4.0, unidade = "un", precoUnitario = 0.45),
            ItemListaEntity(listaId = festa, descricao = "Guardanapos", quantidade = 1.0, unidade = "un", precoUnitario = 1.99),
            ItemListaEntity(listaId = festa, descricao = "Velas", quantidade = 1.0, unidade = "un", precoUnitario = 2.50),
            ItemListaEntity(listaId = festa, descricao = "Balões", quantidade = 1.0, unidade = "un", precoUnitario = 3.99),
        ).forEach { repo.guardarItem(it) }

        val bricolage = repo.guardarLista(
            ListaCompraEntity(nome = "Bricolage", lojaId = leroy)
        )
        listOf(
            ItemListaEntity(listaId = bricolage, descricao = "Tinta branca 5L", quantidade = 1.0, unidade = "un", precoUnitario = 24.90),
            ItemListaEntity(listaId = bricolage, descricao = "Rolo de pintura", quantidade = 1.0, unidade = "un", precoUnitario = 4.50),
            ItemListaEntity(listaId = bricolage, descricao = "Fita cola de pintor", quantidade = 1.0, unidade = "un", precoUnitario = 2.99),
        ).forEach { repo.guardarItem(it) }

        // ------- HISTÓRICO DE PREÇOS (para o ecrã 3) -------
        // Foco no "Azeite 500ml" para reproduzir o mock com evolução ao longo de meses.
        val agora = System.currentTimeMillis()
        val umDia = 24L * 60 * 60 * 1000
        fun hp(produto: String, loja: Long, nomeLoja: String, preco: Double, quandoMillis: Long) =
            HistoricoPrecoEntity(
                produtoNome = produto,
                lojaId = loja,
                nomeLoja = nomeLoja,
                preco = preco,
                data = quandoMillis
            )

        val historico = buildList {
            // Evolução no Minipreço (loja favorita) — descida clara.
            add(hp("Azeite 500ml", minipreco, "Minipreço", 6.89, agora - 120 * umDia))
            add(hp("Azeite 500ml", minipreco, "Minipreço", 6.49, agora - 90 * umDia))
            add(hp("Azeite 500ml", minipreco, "Minipreço", 5.99, agora - 60 * umDia))
            add(hp("Azeite 500ml", minipreco, "Minipreço", 5.99, agora - 14 * umDia))

            // Comparação com outras lojas (data recente distinta).
            add(hp("Azeite 500ml", lidl, "Lidl", 6.20, agora - 5 * umDia))
            add(hp("Azeite 500ml", pingoDoce, "Pingo Doce", 6.49, agora - 21 * umDia))
            add(hp("Azeite 500ml", continente, "Continente", 6.89, agora - 30 * umDia))

            // Outros produtos para o dropdown não estar vazio.
            add(hp("Leite 1L", minipreco, "Minipreço", 1.20, agora - 3 * umDia))
            add(hp("Leite 1L", lidl, "Lidl", 1.15, agora - 4 * umDia))
            add(hp("Leite 1L", pingoDoce, "Pingo Doce", 1.29, agora - 10 * umDia))

            add(hp("Café", minipreco, "Minipreço", 3.40, agora - 7 * umDia))
            add(hp("Café", continente, "Continente", 3.19, agora - 12 * umDia))
            add(hp("Café", lidl, "Lidl", 2.99, agora - 20 * umDia))
        }
        repo.registarPrecos(historico)
    }
}
