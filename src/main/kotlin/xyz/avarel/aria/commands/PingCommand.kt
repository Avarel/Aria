package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.await
import xyz.avarel.core.commands.Command
import xyz.avarel.core.commands.CommandInfo
import xyz.avarel.core.commands.field
import xyz.avarel.core.commands.sendEmbed

@CommandInfo(
    aliases = ["ping"],
    description = "Get the ping of the bot."
)
class PingCommand : Command<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        context.channel.sendEmbed("Pong!") {
            field("REST", true) { context.jda.restPing.await().toString() }
            field("Gateway", true) {
                context.bot.shardManager.averageGatewayPing.toString()
            }
        }.queue()
    }
}