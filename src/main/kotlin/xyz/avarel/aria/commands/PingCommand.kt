package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.await
import xyz.avarel.core.commands.*

class PingCommand : Command<MessageContext> {
    override val aliases = arrayOf("ping")

    override val info = info("Ping Command") {
        desc { "Get the ping of the bot." }
    }

    override suspend operator fun invoke(context: MessageContext) {
        val current = System.currentTimeMillis()
        context.channel.sendTyping().await()
        val elapsed = System.currentTimeMillis() - current

        context.channel.sendEmbed("Pong!") {
            field("REST", true) { elapsed.toString() }
            field("Gateway", true) { context.bot.shardManager.averagePing.toInt().toString() }
        }.queue()
    }
}