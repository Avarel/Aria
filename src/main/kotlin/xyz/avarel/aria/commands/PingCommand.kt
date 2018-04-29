package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.await
import xyz.avarel.core.commands.*

@CommandInfo(
        aliases = ["ping"],
        description = "Get the ping."
)
class PingCommand : AnnotatedCommand<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        val current = System.currentTimeMillis()
        context.channel.sendTyping().await()
        val elapsed = System.currentTimeMillis() - current

        context.channel.sendEmbed("Ping") {
            field("REST", true) { elapsed.toString() }
            field("Gateway", true) { context.bot.shardManager.averagePing.toInt().toString() }
        }.queue()
    }
}