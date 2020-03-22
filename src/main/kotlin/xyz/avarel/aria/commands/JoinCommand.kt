package xyz.avarel.aria.commands

import net.dv8tion.jda.api.entities.VoiceChannel
import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.music.MusicController
import xyz.avarel.aria.music.MusicController.ConnectResult
import xyz.avarel.core.commands.*

@CommandInfo(
    aliases = ["join", "j"],
    description = "Let the bot join your current voice channel."
)
class JoinCommand : Command<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        join(context, true)
    }
}

fun join(context: MessageContext, move: Boolean): MusicController? {
    val vc = context.member?.voiceState?.channel
    if (vc == null) {
        context.channel.sendEmbed("No current voice channel.") {
            desc { "This command requires you to be connected to a voice channel." }
        }.queue()
        return null
    }

    var controller = context.bot.musicManager.getExisting(context.guild.idLong)
    if (controller == null) {
        controller = context.bot.musicManager.createAndPut(context.guild.idLong)
        joinMessage(context, controller, vc)
    } else if (move) joinMessage(context, controller, vc)
    return controller
}

private fun joinMessage(
    context: MessageContext,
    controller: MusicController,
    vc: VoiceChannel
) {
    context.channel.sendEmbed {
        when (controller.connect(vc)) {
            ConnectResult.SUCCESS -> {
                title { "Joined Voice Channel" }
                desc { "The bot has joined your voice channel `${vc.name}`." }
            }
            ConnectResult.USER_LIMIT -> {
                title { "Voice channel is full." }
                desc { "The voice channel `${vc.name}` is completely full." }
            }
            ConnectResult.NO_PERMISSION -> {
                title { "No permission." }
                desc { "The bot does not have permission to join `${vc.name}`." }
            }
        }
    }.queue()
}