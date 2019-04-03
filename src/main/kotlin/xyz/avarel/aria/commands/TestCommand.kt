package xyz.avarel.aria.commands

import kotlinx.coroutines.delay
import xyz.avarel.aria.MessageContext
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
        val duration = context.args.number() * 1000
        val count = count++

        delay(duration.toLong())

        context.channel.sendMessage("Test $count").queue()
    }
}