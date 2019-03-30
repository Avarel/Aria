package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.core.commands.*

class InfoCommand : Command<MessageContext> {
    override val aliases = arrayOf("help", "info", "i", "h")

    override val info = CommandInfo(
            "Information Command",
            Description("Get general information about the bot.")
    )

    override suspend fun invoke(context: MessageContext) {
        context.channel.sendEmbed("Information") {
            desc { "The bot's prefix is `${context.bot.prefix}`." }
            fieldBuilder("Command") {
                for (cmd in context.bot.commandRegistry.entries) {
                    append('`')
                    append(cmd.aliases.first())
                    append("` – ")
                    append(cmd.info.description.text)
                    append("\n")
                }
            }

            footer { "2018 – Built with love by Avarel." }
        }.queue()
    }
}