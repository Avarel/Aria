package xyz.avarel.aria.listener

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.VoiceChannel
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent
import net.dv8tion.jda.core.hooks.EventListener
import org.slf4j.LoggerFactory
import xyz.avarel.aria.Bot

class VoiceListener(private val bot: Bot) : EventListener {
    companion object {
        val LOG = LoggerFactory.getLogger(VoiceListener::class.java)!!
    }

    override fun onEvent(event: Event) {
        when (event) {
            is GuildVoiceJoinEvent -> onGuildVoiceJoin(event)
            is GuildVoiceMoveEvent -> onGuildVoiceMove(event)
            is GuildVoiceLeaveEvent -> onGuildVoiceLeave(event)
        }
    }

    private fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        join(event.guild, event.channelJoined)
    }

    private fun onGuildVoiceMove(event: GuildVoiceMoveEvent) {
        if (event.member == event.guild.selfMember) {
            val controller = bot.musicManager.getExisting(event.guild.idLong)!!
            controller.connect(event.channelJoined)
        }

        join(event.guild, event.channelJoined)
    }

    private fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        if (event.member == event.guild.selfMember) {
            bot.musicManager.destroy(event.guild.idLong)
            return
        }
        left(event.guild, event.channelLeft)
    }

    private fun join(guild: Guild, channelJoined: VoiceChannel) {
        bot.musicManager.getExisting(guild.idLong)?.let { controller ->
            if (channelJoined == controller.channel) {
                if (controller.isAlone!!) {
                    controller.autoDestroy(true)
                    if (controller.player.playingTrack != null) {
                        controller.player.isPaused = true
                        LOG.debug("Paused playback for $guild.")
                    }
                } else {
                    controller.autoDestroy(false)
                    if (controller.player.playingTrack != null) {
                        controller.player.isPaused = false
                        LOG.debug("Resumed playback for $guild.")
                    }
                }
            }
        }
    }

    private fun left(guild: Guild, channelLeft: VoiceChannel) {
        bot.musicManager.getExisting(guild.idLong)?.let { controller ->
            if (channelLeft == controller.channel && controller.isAlone!!) {
                controller.autoDestroy(true)
                if (controller.player.playingTrack != null) {
                    controller.player.isPaused = true
                    LOG.debug("Paused playback for $guild.")
                }
            }
        }
    }
}
