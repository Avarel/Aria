package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.progressBarTo
import xyz.avarel.aria.utils.requireMusicControllerMessage
import xyz.avarel.core.commands.*

class VolumeCommand : Command<MessageContext> {
    override val aliases = arrayOf("volume", "v")

    override val info = CommandInfo(
            "Volume Command",
            Description("Set the volume of the music player."),
            Usage(Argument.Percentage)
    )

    override suspend operator fun invoke(context: MessageContext) {
        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
                ?: return requireMusicControllerMessage(context)

        val original = controller.player.volume
        if (context.args.isNotEmpty()) {
            val volume = context.args[0].toIntOrNull()?.coerceIn(0, 150)

            if (volume == null) {
                context.channel.sendEmbed("Invalid Argument") {
                    desc { "${context.args[0]} is not a valid number from 0–150." }
                }.queue()
                return
            }

            controller.player.volume = volume
            context.bot.store[context.guild.id, "music", "volume"].setInt(volume)
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