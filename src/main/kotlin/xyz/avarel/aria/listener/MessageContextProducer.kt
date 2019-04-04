package xyz.avarel.aria.listener

import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.EventListener
import xyz.avarel.aria.Bot
import xyz.avarel.aria.utils.MessageContext
import xyz.avarel.core.commands.Command
import xyz.avarel.core.commands.Dispatcher
import java.util.ArrayList

/**
 * Label implementation where an [GuildMessageReceivedEvent] is filtered
 * from the event stream produced by [JDA].
 *
 * The event is then processed through checking for the prefix, stripping
 * the usages, and parsing the usages into the [MessageContext] class.
 *
 * The message context is then published to the stream.
 *
 * @author Avarel
 */
class MessageContextProducer(
        private val bot: Bot,
        @Suppress("EXPERIMENTAL_API_USAGE")
        private val dispatcher: Dispatcher<MessageContext, Command<MessageContext>>
) : EventListener {
    private val argumentPattern = Regex("`{3}(?:\\w+\\n)?([\\s\\S]*?)`{3}|`([^`]+)`|(\\S+)")

    private fun stringSplit(s: String): List<String> {
        val parts = ArrayList<String>()

        val matcher = argumentPattern.toPattern().matcher(s)

        outer@while (matcher.find()) {
            for (i in 1..matcher.groupCount()) {
                val group = matcher.group(i)
                if (group != null) {
                    parts.add(group)
                    continue@outer
                }
            }
        }

        return parts
    }

    override fun onEvent(event: Event) {
        if (event is GuildMessageReceivedEvent) {
            if (event.message.contentRaw.startsWith(bot.prefix)) {
                val list = stringSplit(event.message.contentRaw)

                @Suppress("EXPERIMENTAL_API_USAGE")
                dispatcher.offer(MessageContext(bot, event.message, list[0].substring(bot.prefix.length).trim(), list.subList(1, list.size)))
            }
        }
    }
}