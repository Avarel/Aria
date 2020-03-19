package xyz.avarel.aria.music

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import com.sedmelluq.discord.lavaplayer.track.AudioTrack

/**
 * Context class for an [AudioTrack].
 *
 * @param requester The [Member] that requested this track.
 * @param requestChannel The [TextChannel] that the track was requested in.
 */
data class TrackContext(val requester: Member, val requestChannel: TextChannel)