package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.core.commands.*

@CommandInfo(
    aliases = ["help", "info", "i", "h"],
    description = "Get general information about the bot."
)
class InfoCommand : Command<MessageContext> {
    override suspend fun invoke(context: MessageContext) {
        context.channel.sendEmbed("Information") {
            desc { "The bot's prefix is `${context.bot.prefix}`." }

            fieldBuilder("Command") {
                for (cmd in context.bot.commandRegistry.entries) {
                    if (!cmd.info.visible) continue
                    append('`')
                    append(cmd.info.aliases.first())
                    append("` – ")
                    append(cmd.info.description)
                    appendln()
                }
            }

            footer { "2018 – Built with love by Avarel." }
        }.queue()
    }
}