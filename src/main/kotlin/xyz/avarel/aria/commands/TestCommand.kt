package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.dsl
import xyz.avarel.core.commands.Command
import xyz.avarel.core.commands.info

class TestCommand : Command<MessageContext> {
    override val aliases = arrayOf("test")

    override val info = info("Test Command") {
        desc { "You're not supposed to see this." }
        visible { false }
    }

    private var count = 0

    override suspend operator fun invoke(context: MessageContext) {
        dsl(context.arguments) {
            integer {
                println("int path")
            }
            string {
                println("string path")
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