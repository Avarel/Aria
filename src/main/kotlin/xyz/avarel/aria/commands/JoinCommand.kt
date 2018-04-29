package xyz.avarel.aria.commands

import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.music.MusicController.*
import xyz.avarel.core.commands.*

@CommandInfo(
        aliases = ["join", "j"],
        description = "Let the bot join your current voice channel."
)
class JoinCommand : AnnotatedCommand<MessageContext> {
    override suspend operator fun invoke(context: MessageContext) {
        val vc = context.member.voiceState.channel
        if (vc == null) {
            context.channel.sendEmbed("No current voice channel.") {
                desc { "This command requires you to be connected to a voice channel." }
            }.queue()
            return
        }

        val controller = context.bot.musicManager.getOrCreate(context.guild.idLong)

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
}