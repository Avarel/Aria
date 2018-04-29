package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.core.commands.*

@CommandInfo(
        aliases = ["help", "info", "i", "h"],
        description = "General information about the bot."
)
class InfoCommand : AnnotatedCommand<MessageContext> {
    override suspend fun invoke(context: MessageContext) {
        context.channel.sendEmbed("Information") {
            desc { "The bot's prefix is `${context.bot.prefix}`." }
            fieldBuilder("Command") {
                for (cmd in context.bot.commandRegistry.entries) {
                    append('`')
                    append(cmd.aliases.first())
                    append("` – ")
                    append(cmd.description)
                    append("\n")
                }
            }

            footer { "2018 – Built with love by Avarel." }
        }.queue()
    }
}