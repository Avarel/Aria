package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.dsl
import xyz.avarel.core.commands.Command
import xyz.avarel.core.commands.CommandInfo

@CommandInfo(
    aliases = ["test"],
    description = "You're not supposed to see this.",
    visible = false
)
class TestCommand : Command<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        context.dsl {
            integer {
                context.channel.sendMessage("Got an integer $it").queue()
            }
            time {
                context.channel.sendMessage("Got a time $it").queue()
            }
            match(arrayOf("dank", "meme"), "wow") {
                context.channel.sendMessage("Got a match $it").queue()
            }
        }
    }
}