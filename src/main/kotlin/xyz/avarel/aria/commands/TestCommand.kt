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
    private var count = 0

    override suspend operator fun invoke(context: MessageContext) {
        context.dsl {
            integer {
                context.channel.sendMessage("Got an integer $it").queue()
            }
            time {
                context.channel.sendMessage("Got a time $it").queue()
            }
            match("dank", "meme", desc = "wow") {
                context.channel.sendMessage("Got a match $it").queue()
            }
        }
//        argParse(context) {
//            matchInt(description = "A number for fun.") {
//                matchLabel("love", "do you want extra love?") {
//                    context.channel.sendMessage("I love you! $it").queue()
//                    end()
//                }.otherwiseMatchInt {
//                    context.channel.sendMessage("NANI").queue()
//                    end()
//                }.otherwiseError()
//            }.otherwiseNoMore {
//                context.channel.sendMessage("wooooo").queue()
//                end()
//            }
//        }
    }
}