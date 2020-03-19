@file:JvmMultifileClass

package xyz.avarel.aria.music.relay

sealed class MusicInput

class JoinMusicChannelInput(val channelId: Long): MusicInput()
class MoveMusicChannelInput(val channelId: Long): MusicInput()
object LeaveMusicChannelInput : MusicInput()
class PlayMusicInput(val query: String): MusicInput()
class ChangeVolumeInput(val volume: Int): MusicInput()
class ChangeRepeatMusicInput(val repeatMode: Int): MusicInput()
class PlaybackMusicInput(val pause: Boolean): MusicInput()
class RemoveTrackInput(val index: Int): MusicInput()
class RemoveTrackRangeInput(val low: Int, val high: Int): MusicInput()
object ClearQueueInput : MusicInput()
class SeekInput(val position: Long): MusicInput()

abstract class AbstractMusicInputListener {
    fun onInput(event: MusicInput) {
        when (event) {
            is JoinMusicChannelInput -> onJoinMusicChannelInput(event)
            is LeaveMusicChannelInput -> onLeaveMusicChannelInput(event)
            is MoveMusicChannelInput -> onMoveMusicChannelInput(event)
            is PlayMusicInput -> onPlayMusicInput(event)
            is ChangeVolumeInput -> onChangeVolumeInput(event)
            is ChangeRepeatMusicInput -> onChangeRepeatMusicInput(event)
            is PlaybackMusicInput -> onPlaybackMusicInput(event)
            is RemoveTrackInput -> onRemoveTrackInput(event)
            is RemoveTrackRangeInput -> onRemoveTrackRangeInput(event)
            is ClearQueueInput -> onClearQueueInput(event)
            is SeekInput -> onSeekInput(event)
        }
    }

    abstract fun onJoinMusicChannelInput(event: JoinMusicChannelInput)
    abstract fun onLeaveMusicChannelInput(event: LeaveMusicChannelInput)
    abstract fun onMoveMusicChannelInput(event: MoveMusicChannelInput)
    abstract fun onPlayMusicInput(event: PlayMusicInput)
    abstract fun onChangeVolumeInput(event: ChangeVolumeInput)
    abstract fun onChangeRepeatMusicInput(event: ChangeRepeatMusicInput)
    abstract fun onPlaybackMusicInput(event: PlaybackMusicInput)
    abstract fun onRemoveTrackInput(event: RemoveTrackInput)
    abstract fun onRemoveTrackRangeInput(event: RemoveTrackRangeInput)
    abstract fun onClearQueueInput(event: ClearQueueInput)
    abstract fun onSeekInput(event: SeekInput)
}