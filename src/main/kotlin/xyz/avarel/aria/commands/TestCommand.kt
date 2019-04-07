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
            val what = expectInt(description = "?")
            println(what)
        }
    }
}