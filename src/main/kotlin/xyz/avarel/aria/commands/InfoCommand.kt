package xyz.avarel.aria.commands

import xyz.avarel.aria.utils.MessageContext
import xyz.avarel.aria.utils.errorMessage
import xyz.avarel.aria.utils.partition
import xyz.avarel.core.commands.*

@CommandInfo(
        aliases = ["help", "info", "i", "h", "?"],
        title = "Information Command",
        description = "Get general information about the bot.",
        usage = "[command name]"
)
class InfoCommand : AnnotatedCommand<MessageContext>() {
    override suspend fun invoke(context: MessageContext) {
        context.parse {
            val num = optionalInt()
            if (num == null && hasNext()) {
                val label = expectString("Specific command name.", "[command name]")
                val cmd = context.bot.commandRegistry[label]
                        ?: return context.errorMessage("Command `$label` does not exist.")

                context.channel.sendEmbed(cmd.title) {
                    field("All Aliases") { cmd.aliases.joinToString("` `", "`", "`") }
                    field("Description") { cmd.description }
                    field("Usage") { "```${cmd.aliases[0]} ${cmd.usage}```" }
                }.queue()
                return
            }

            context.channel.sendEmbed("Information") {
                descBuilder {
                    appendln("The bot's prefix is `${context.bot.prefix}`.")
                    appendln("Want more information from a command? Try `${aliases[0]} [command name]`!")
                    append("2018-2019 – Built with love by Avarel.")
                }

                val itemsPerPage = 12
                val pages = context.bot.commandRegistry.entries.toList().partition(itemsPerPage)

                val pg = num?.coerceIn(1, pages.size) ?: 1

                val list = pages[pg - 1]

                fieldBuilder("Available Commands") {
                    for (cmd in list) {
                        if (!cmd.visible) continue
                        append('`')
                        append(cmd.aliases.first())
                        append("` – ")
                        append(cmd.description)
                        appendln()
                    }
                }

                footer { "Page $pg/${pages.size}" }
            }.queue()
        }
    }
}