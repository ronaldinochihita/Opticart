# Opticart — Gestão de Compras

App móvel Android desenvolvida em **Kotlin** + **Jetpack Compose** para a
unidade curricular de **Programação de Aplicação do Lado do Cliente (PAC)** e
alinhada com o desenho UX/UI do trabalho de **Sistemas Interativos (SI)** —
Curso de Tecnologias Web e Dispositivos Móveis, ESTG Beja, 2.º semestre
2025/2026.

## Personas

- **Utilizador Comum (Organizador)** — usa a app em casa. Gere lojas, cria e
  edita listas de compras, consulta o histórico de preços por produto e loja.
- **Utilizador In-Store (Executor)** — usa a app no supermercado. Percorre a
  lista em "Modo Compra", com botões grandes, arredondamento a 5 cêntimos e
  resumo final que alimenta o histórico.

## Ecrãs implementados

| # | Ecrã | Rota |
|---|---|---|
| 1 | Listas (home) | `listas` |
| 2 | Edição de Lista | `edicao/{listaId}` |
| 3 | Histórico de Preços | `historico` |
| 4 | Gestão de Lojas | `lojas` |
| 5 | Modo Compra | `modo/{listaId}` |
| 6 | Resumo Final | `resumo/{listaId}` |

Navegação com bottom tab bar entre Listas · Histórico · Lojas; os ecrãs de
detalhe (Edição, Modo Compra, Resumo Final) abrem em ecrã cheio.

## Stack técnico

- **Kotlin 2.0.21** + **Jetpack Compose** (Material 3, BOM 2024.10)
- **MVVM** — UI Compose ↔ ViewModel (StateFlow) ↔ Repository ↔ Room DAO
- **Room 2.6.1** com **KSP** para geração de código
- **Navigation Compose 2.8.4**
- **Coroutines 1.9** + Flow para observação reactiva
- **Gradle 8.9** (Kotlin DSL + Version Catalog)
- **min SDK 24** · **target SDK 35**

## Base de dados (Room)

Cinco entidades:

- `LojaEntity` — nome, morada, horário, favorita
- `ListaCompraEntity` — cabeçalho da lista (FK opcional para loja)
- `ItemListaEntity` — itens de uma lista (FK para lista, cascade)
- `HistoricoCompraEntity` — snapshot de cada compra finalizada
- `HistoricoPrecoEntity` — preço de um produto numa loja num dado momento
  (alimenta o ecrã de comparação e evolução)

O esquema gerado pelo Room é exportado para
[`app/schemas/pt.ipbeja.gestaocompras.data.local.AppDatabase/`](app/schemas/pt.ipbeja.gestaocompras.data.local.AppDatabase/)
— usar como evidência directa da secção 8 do relatório PAC.

Dados de exemplo são semeados no primeiro arranque via
[`SeedData`](app/src/main/java/pt/ipbeja/gestaocompras/data/seed/SeedData.kt)
(5 lojas, 3 listas, ~30 itens, 13 registos de preço).

## Estrutura do módulo `app`

```
app/src/main/java/pt/ipbeja/gestaocompras/
├── MainActivity.kt
├── OpticartApplication.kt          # ponto de entrada + Repository singleton
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── dao/                    # LojaDao, ListaCompraDao, ItemListaDao,
│   │   │                           # HistoricoDao, HistoricoPrecoDao
│   │   └── entity/                 # as 5 @Entity
│   ├── repository/ComprasRepository.kt
│   └── seed/SeedData.kt
├── domain/model/                   # Persona (enum)
└── ui/
    ├── ViewModelFactory.kt         # factory única (sem Hilt)
    ├── theme/                      # Color.kt, Type.kt, Theme.kt
    ├── components/                 # StoreIcon, ItemAvatar, OpticartCard, EmptyState
    ├── nav/                        # Rotas.kt, AppNavigation.kt
    ├── organizador/
    │   ├── listas/                 # ListasScreen + ViewModel
    │   ├── edicao/                 # EdicaoListaScreen + ViewModel
    │   ├── historico/              # HistoricoScreen + ViewModel
    │   └── lojas/                  # LojasScreen + ViewModel
    └── executor/
        ├── modo/                   # ModoCompraScreen + ViewModel
        └── resumo/                 # ResumoFinalScreen + ViewModel
```

## Como abrir

1. Android Studio **Ladybug** ou superior (necessário para Kotlin 2.0 e
   Compose Compiler Plugin).
2. `File → Open…` e seleccionar a pasta raiz do projecto.
3. Aguardar o **Gradle sync** (Gradle 8.9, dependências, KSP).
4. Ligar um dispositivo por USB (com depuração activa) ou arrancar um
   emulador, e clicar **Run ▶**.

## Alinhamento com o trabalho de SI

Os ecrãs seguem os mockups Balsamiq entregues no TG1 de Sistemas Interativos
(equipa comum). A paleta verde Opticart, o cartão da loja seleccionada, a
barra de "TOTAL ATUAL" com arredondamento e o ecrã de comparação por loja
correspondem 1-para-1 aos ecrãs 1 a 5 do documento entregue.

## Trabalho académico

- **UC:** Programação de Aplicação do Lado do Cliente (PAC)
- **Escola:** Escola Superior de Tecnologia e Gestão de Beja (IPBeja)
- **Ano lectivo:** 2025/2026
