package xyz.avarel.aria.commands

import xyz.avarel.aria.utils.MessageContext
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.core.commands.AnnotatedCommand
import xyz.avarel.core.commands.CommandInfo
import xyz.avarel.core.commands.desc
import xyz.avarel.core.commands.sendEmbed

@CommandInfo(
        aliases = ["leave", "l"],
        title = "Leave Music Channel Command",
        description = "Make the bot leave its current voice channel."
)
class LeaveCommand : AnnotatedCommand<MessageContext>() {
    override suspend operator fun invoke(context: MessageContext) {
        val instance = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        val vc = instance.channel // assumption
        instance.destroy()

        context.channel.sendEmbed("Left Voice Channel") {
            desc { "The bot has left the voice channel `${vc.name}`." }
        }.queue()
    }
}