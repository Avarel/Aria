package xyz.avarel.aria.utils

import net.dv8tion.jda.core.entities.Message
import xyz.avarel.aria.Bot
import xyz.avarel.core.commands.Context

/**
 * [Context] wrapper for [Message].
 *
 * @author Avarel
 */
data class MessageContext internal constructor(
        val bot: Bot,
        private val message: Message,
        override val label: String,
        override val arguments: List<String>
) : Context, Message by message {
    private lateinit var _ag: ArgumentParser

    val parser: ArgumentParser get() {
        if (!::_ag.isInitialized) {
            _ag = ExpectArgumentParser(this)
        }
        return _ag
    }

    inline fun parse(block: ExpectArgumentParser.() -> Unit) {
        (parser as ExpectArgumentParser).also {
            try {
                it.block()
            } catch (e: ExpectArgumentException) {
                it.matchError()
            }
        }
    }
}
