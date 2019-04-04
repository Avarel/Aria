package xyz.avarel.aria.commands

import net.dv8tion.jda.core.entities.MessageEmbed
import xyz.avarel.aria.MessageContext
import xyz.avarel.core.commands.*

class InfoCommand : Command<MessageContext> {
    override val aliases = arrayOf("help", "info", "i", "h")

    override val info = info("Information Command") {
        desc { "Get general information about the bot." }
    }

    override suspend fun invoke(context: MessageContext) {
        context.channel.sendEmbed("Information") {
            desc { "The bot's prefix is `${context.bot.prefix}`." }

            fieldBuilder("Command") {
                for (cmd in context.bot.commandRegistry.entries) {
                    if (!cmd.info.visible) continue
                    append('`')
                    append(cmd.aliases.first())
                    append("` – ")
                    append(cmd.info.description ?: "No description.")
                    appendln()
                }
            }

            footer { "2018 – Built with love by Avarel." }
        }.queue()
    }

    fun renderUsage(info: ArgumentInfo, level: Int = 0): String = buildString {

    }
}