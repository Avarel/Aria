package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.argParse
import xyz.avarel.core.commands.*

class TestCommand : Command<MessageContext> {
    override val aliases = arrayOf("test")

    override val info = info("Test Command") {
        desc { "You're not supposed to see this." }
        visible { false }
    }

    @Volatile
    private var count = 0

    override suspend operator fun invoke(context: MessageContext) {
        argParse(context) {
            matchInt(description = "A number for fun.") {
                matchLabel("love", "do you want extra love?") {
                    context.channel.sendMessage("I love you! $it").queue()
                    end()
                }.otherwiseError()
            }.otherwiseNoMore {
                context.channel.sendMessage("wooooo").queue()
                end()
            }
        }
    }
}