import kotlinx.coroutines.delay

// --- Representação segura dos status ---
sealed class PedidoStatus {
    object Novo : PedidoStatus()
    object Processado : PedidoStatus()
    // Qualquer novo status pode ser adicionado como um novo objeto/class
}

data class Pedido(val id: Int, val status: PedidoStatus)

// --- Interface da estratégia ---
interface ProcessadorStatus {
    suspend fun processar(pedido: Pedido)
}

// --- Estratégias concretas ---
class ProcessadorNovo : ProcessadorStatus {
    override suspend fun processar(pedido: Pedido) {
        println("Pedido em processamento: ${pedido.id}")
        delay(2000) // simula processamento assíncrono sem bloquear a thread
    }
}

class ProcessadorProcessado : ProcessadorStatus {
    override suspend fun processar(pedido: Pedido) {
        println("Pedido já processado: ${pedido.id}")
    }
}

class ProcessadorDesconhecido : ProcessadorStatus {
    override suspend fun processar(pedido: Pedido) {
        println("Status desconhecido do pedido: ${pedido.id}")
    }
}

// --- Processador principal (coordenador) ---
class PedidoProcessor(
    private val processadores: Map<Class<out PedidoStatus>, ProcessadorStatus> = mapOf(
        PedidoStatus.Novo::class.java to ProcessadorNovo(),
        PedidoStatus.Processado::class.java to ProcessadorProcessado()
    ),
    private val processadorDesconhecido: ProcessadorStatus = ProcessadorDesconhecido()
) {
    suspend fun processarPedido(pedido: Pedido) {
        val processador = processadores[pedido.status::class.java] ?: processadorDesconhecido
        processador.processar(pedido)
    }
}
