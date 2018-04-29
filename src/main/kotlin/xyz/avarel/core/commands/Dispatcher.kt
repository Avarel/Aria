package xyz.avarel.core.commands

import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import org.slf4j.LoggerFactory
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Dispatches a command based on the incoming context [CTX].
 *
 * If there isn't a valid command based on the context, then the
 * context will simply ignore and discard the event.
 * If there is a valid command for the context, the
 * context will execute the command.
 *
 * The response is then published to the stream.
 *
 * @param  CTX
 *         Context type.
 * @param  registry
 *         The command registry.
 * @param  context
 *         The executor service.
 * @author Avarel
 */
class Dispatcher<in CTX: Context, in C: Command<CTX>>(
        private val registry: CommandRegistry<C>,
        private val context: CoroutineContext = DefaultDispatcher
): SendChannel<CTX> by actor<CTX>(context, block = {
    for (ctx in channel) {
        registry[ctx.label]?.let { cmd ->
            try {
                cmd(ctx)
            } catch (e: Exception) {
                LOG.error("Error while executing command ${ctx.label}.", e)
            }
        }
    }
}) {
    companion object {
        val LOG = LoggerFactory.getLogger(Dispatcher::class.java)!!
    }
}