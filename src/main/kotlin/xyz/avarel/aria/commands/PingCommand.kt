package xyz.avarel.aria.commands

import xyz.avarel.aria.utils.MessageContext
import xyz.avarel.aria.utils.await
import xyz.avarel.core.commands.AnnotatedCommand
import xyz.avarel.core.commands.CommandInfo
import xyz.avarel.core.commands.field
import xyz.avarel.core.commands.sendEmbed

@CommandInfo(
        aliases = ["ping"],
        title = "Ping Command",
        description = "Get the ping of the bot."
)
class PingCommand : AnnotatedCommand<MessageContext>() {
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