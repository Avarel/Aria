//@file:JvmMultifileClass
//
//package xyz.avarel.aria.music.relay
//
//import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager
//import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
//import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioTrack
//import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager
//import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioTrack
//import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
//import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioTrack
//import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
//import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioTrack
//import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
//import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioTrack
//import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
//import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack
//import com.sedmelluq.discord.lavaplayer.track.AudioTrack
//import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo

//import org.apache.http.client.config.CookieSpecs
//import org.apache.http.client.config.RequestConfig
//import xyz.avarel.aria.music.RepeatMode
//
//sealed class MusicQuery<T: MusicOutput> // no state changes
//sealed class MusicInput<T: MusicOutput> // changes
//sealed class MusicOutput
//
//object NoSongError: MusicOutput()
//
//object ChannelQuery : MusicQuery<ChannelOutput>()
//class ChannelOutput(val channelId: Long): MusicOutput()
//
//class ChannelJoinInput(val targetChannelId: Long): MusicInput<ChannelJoinOutput>()
//class ChannelJoinOutput(val result: ConnectResult): MusicOutput()
//
//object ChannelLeaveInput : MusicInput<ChannelLeaveOutput>()
//class ChannelLeaveOutput(val leftChannelId: Long): MusicOutput()
//
//class ChannelMoveInput(val targetChannelId: Long): MusicInput<ChannelMoveOutput>()
//class ChannelMoveOutput(val result: ConnectResult?): MusicOutput()
//
//class PlayInput(val songs: List<MusicTrack>): MusicInput<PlayOutput>()
//class PlayOutput(val addedSongs: List<MusicTrack>): MusicOutput()
//
//object VolumeQuery : MusicQuery<VolumeOutput>()
//class VolumeOutput(val volume: Int): MusicOutput()
//class VolumeChangeInput(val volume: Int): MusicInput<VolumeChangeOutput>()
//class VolumeChangeOutput(val oldVolume: Int, newVolume: Int): MusicOutput()
//
//object RepeatModeQuery : MusicQuery<RepeatModeOutput>()
//class RepeatModeOutput(val repeatMode: RepeatMode): MusicOutput()
//class RepeatModeChangeInput(val repeatMode: RepeatMode): MusicInput<RepeatModeChangeOutput>()
//class RepeatModeChangeOutput(val old: RepeatMode, val new: RepeatMode): MusicOutput()
//
//object PlaybackQuery : MusicQuery<PlaybackOutput>()
//class PlaybackChangeInput(val pause: Boolean): MusicInput<PlaybackOutput>()
//class PlaybackOutput(val isPaused: Boolean): MusicOutput()
//class PlaybackChangePositionInput(val position: Long): MusicInput<PlaybackChangePositionOutput>()
//class PlaybackChangePositionOutput(val oldPosition: Long, newPosition: Long): MusicOutput()
//
//object QueueQuery: MusicQuery<QueueOutput>()
//class QueueOutput(val songs: List<MusicTrack>): MusicOutput()
//class QueueRemoveInput(val index: Int): MusicInput<QueueRemoveOutput>()
//class QueueRemoveOutput(val removedSongs: List<MusicTrack>): MusicOutput()
//object QueueClearInput : MusicInput<QueueClearOutput>()
//class QueueClearOutput(val success: Boolean): MusicOutput()
//
//data class MusicTrack(
//        val source: TrackSource,
//        val id: String,
//        val position: Long,
//        val duration: Long,
//        val title: String,
//        val author: String,
//        val stream: Boolean,
//        val uri: String
//)
//
//enum class TrackSource(val sourceManager: AudioSourceManager) {
//    YOUTUBE(YoutubeAudioSourceManager().apply {
//        configureRequests { config ->
//            RequestConfig.copy(config).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build()
//        }
//    }),
//    SOUNDCLOUD(SoundCloudAudioSourceManager()),
//    BANDCAMP(BandcampAudioSourceManager()),
//    VIMEO(VimeoAudioSourceManager()),
//    TWITCH(TwitchStreamAudioSourceManager()),
//    BEAM(BeamAudioSourceManager());
//
//    fun reconsitute(track: MusicTrack): AudioTrack {
//        val info = AudioTrackInfo(track.title, track.author, track.duration, track.id, track.stream, track.uri)
//        return when (this) {
//            YOUTUBE -> YoutubeAudioTrack(info, sourceManager as YoutubeAudioSourceManager)
//            SOUNDCLOUD -> SoundCloudAudioTrack(info, sourceManager as SoundCloudAudioSourceManager)
//            BANDCAMP -> BandcampAudioTrack(info, sourceManager as BandcampAudioSourceManager)
//            VIMEO -> VimeoAudioTrack(info, sourceManager as VimeoAudioSourceManager)
//            TWITCH -> TwitchStreamAudioTrack(info, sourceManager as TwitchStreamAudioSourceManager)
//            BEAM -> BeamAudioTrack(info, sourceManager as BeamAudioSourceManager)
//        }
//    }
//}
//
//fun sourceOf(track: AudioTrack): TrackSource {
//    return when (track) {
//        is YoutubeAudioTrack -> TrackSource.YOUTUBE
//        is SoundCloudAudioTrack -> TrackSource.SOUNDCLOUD
//        is BandcampAudioTrack -> TrackSource.BANDCAMP
//        is VimeoAudioTrack -> TrackSource.VIMEO
//        is TwitchStreamAudioTrack -> TrackSource.TWITCH
//        is BeamAudioTrack -> TrackSource.BEAM
//        else -> error("Unknown track source")
//    }
//}
//
//fun AudioTrack.toMusicTrack(): MusicTrack {
//    return MusicTrack(
//            sourceOf(this),
//            identifier,
//            position,
//            duration,
//            info.title,
//            info.author,
//            info.isStream,
//            info.uri
//    )
//}
//
///**
// * Result of trying to connect to a voice channel.
// */