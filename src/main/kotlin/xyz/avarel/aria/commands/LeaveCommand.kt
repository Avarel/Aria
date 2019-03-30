package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.core.commands.*

class LeaveCommand : Command<MessageContext> {
    override val aliases = arrayOf("leave", "l")

    override val info = CommandInfo(
            "Leave Music Channel Command",
            Description("Make the bot leave its current voice channel.")
    )

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