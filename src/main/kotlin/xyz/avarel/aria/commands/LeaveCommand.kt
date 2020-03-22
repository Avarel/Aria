package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.dsl
import xyz.avarel.aria.utils.requireMusic
import xyz.avarel.core.commands.Command
import xyz.avarel.core.commands.CommandInfo
import xyz.avarel.core.commands.desc
import xyz.avarel.core.commands.sendEmbed

@CommandInfo(
    aliases = ["leave", "l", "stop"],
    description = "Make the bot leave its current voice channel."
)
class LeaveCommand : Command<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        context.dsl {
            requireMusic { controller ->
                val vc = controller.channel!!.name // assumption
                controller.destroy()

                context.channel.sendEmbed("Left Voice Channel") {
                    desc { "The bot has left the voice channel `$vc`." }
                }.queue()
            }
        }
    }
}