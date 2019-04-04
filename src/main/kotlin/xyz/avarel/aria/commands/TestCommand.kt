package xyz.avarel.aria.commands

import xyz.avarel.aria.utils.MessageContext
import xyz.avarel.core.commands.*

class TestCommand : Command<MessageContext> {
    override val aliases = arrayOf("test")

    override val info = info("Test Command") {
        desc { "You're not supposed to see this." }
        visible { false }
    }

    override suspend operator fun invoke(context: MessageContext) {
        context.parse {
            matchLabel("what is love") {
                val value = expectInt()
                context.channel.sendMessage("baby dont hurt me $value").queue()
            } || matchInt(description = "A number for fun.") {
                matchLabel("love", "do you want extra love?") {
                    context.channel.sendMessage("I love you! $it").queue()
                } || matchLabel("test", "Wowee") {
                    context.channel.sendMessage("What $it").queue()
                } || matchError()
            } || matchError()
        }
    }
}