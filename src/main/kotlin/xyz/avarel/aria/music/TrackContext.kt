package xyz.avarel.aria.music

import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.TextChannel

/**
 * Context for an [com.sedmelluq.discord.lavaplayer.track.AudioTrack].
 */
data class TrackContext(val requester: Member, val channel: TextChannel)