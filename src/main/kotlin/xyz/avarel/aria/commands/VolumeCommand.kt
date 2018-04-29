package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.progressBarTo
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.core.commands.*

@CommandInfo(
        aliases = ["volume", "v"],
        description = "Set the volume of the music player.",
        usage = "[%]"
)
class VolumeCommand : AnnotatedCommand<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        val original = controller.player.volume
        if (context.args.isNotEmpty()) {
            val volume = context.args[0].toIntOrNull()

            if (volume == null) {
                context.channel.sendEmbed("Invalid Argument") {
                    desc { "${context.args[0]} is not a valid number from 0–150." }
                }.queue()
                return
            }

            controller.player.volume = volume.coerceIn(0, 150)
        }

        context.channel.sendEmbed("Volume") {
            descBuilder {
                append("\uD83D\uDD0A ")

                progressBarTo(this, 30, controller.player.volume.toDouble() / 150.0, prefix = "`", suffix = "`")

                if (original != controller.player.volume) {
                    append(" `")
                    append(original)
                    append("%` →")
                }

                append(" `")
                append(controller.player.volume)
                append("%`")
            }
        }.queue()
    }
}