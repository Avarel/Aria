package xyz.avarel.aria

import net.dv8tion.jda.api.entities.Message
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
) : Context, Message by message