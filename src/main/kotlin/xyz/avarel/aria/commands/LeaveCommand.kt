package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.core.commands.Command
import xyz.avarel.core.commands.desc
import xyz.avarel.core.commands.info
import xyz.avarel.core.commands.sendEmbed

class LeaveCommand : Command<MessageContext> {
    override val aliases = arrayOf("leave", "l")

    override val info = info("Leave Music Channel Command") {
        desc { "Make the bot leave its current voice channel." }
    }

    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        val vc = controller.channel!!.name // assumption
        controller.destroy()

        context.channel.sendEmbed("Left Voice Channel") {
            desc { "The bot has left the voice channel `$vc`." }
        }.queue()
    }
}