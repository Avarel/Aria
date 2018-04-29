package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.core.commands.*

@CommandInfo(
        aliases = ["leave", "l"],
        description = "Make the bot leave its current voice channel."
)
class LeaveCommand : AnnotatedCommand<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        val vc = controller.channel!! // assumption
        controller.destroy()

        context.channel.sendEmbed("Left Voice Channel") {
            desc { "The bot has left the voice channel `${vc.name}`." }
        }.queue()
    }
}