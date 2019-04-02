@file:JvmMultifileClass

package xyz.avarel.aria.music.relay

abstract class MusicInput

class JoinMusicChannelInput(val channelId: Long): MusicInput()
class LeaveMusicChannelInput: MusicInput()
class MoveMusicChannelInput(val channelId: Long): MusicInput()
class PlayMusicInput(val query: String): MusicInput()
class ChangeVolumeInput(val volume: Int): MusicInput()
class ChangeRepeatMusicInput(val repeatMode: Int): MusicInput()
class PlaybackMusicInput(val pause: Boolean): MusicInput()
class RemoveTrackMessage(val index: Int): MusicInput()
class ClearQueueMessage: MusicInput()
class SeekMessage(val position: Long): MusicInput()

abstract class MusicInputListener {
    fun onMessage(event: MusicInput) {
        when (event) {
            is JoinMusicChannelInput -> onJoinMusicChannelInput(event)
            is LeaveMusicChannelInput -> onLeaveMusicChannelInput(event)
            is MoveMusicChannelInput -> onMoveMusicChannelInput(event)
            is PlayMusicInput -> onPlayMusicInput(event)
            is ChangeVolumeInput -> onChangeVolumeInput(event)
            is ChangeRepeatMusicInput -> onChangeRepeatMusicInput(event)
            is PlaybackMusicInput -> onPlaybackMusicInput(event)
            is RemoveTrackMessage -> onRemoveTrackCommand(event)
            is ClearQueueMessage -> onClearQueueCommand(event)
            is SeekMessage -> onSeekCommand(event)
        }
    }

    abstract fun onJoinMusicChannelInput(event: JoinMusicChannelInput)
    abstract fun onLeaveMusicChannelInput(event: LeaveMusicChannelInput)
    abstract fun onMoveMusicChannelInput(event: MoveMusicChannelInput)
    abstract fun onPlayMusicInput(event: PlayMusicInput)
    abstract fun onChangeVolumeInput(event: ChangeVolumeInput)
    abstract fun onChangeRepeatMusicInput(event: ChangeRepeatMusicInput)
    abstract fun onPlaybackMusicInput(event: PlaybackMusicInput)
    abstract fun onRemoveTrackCommand(event: RemoveTrackMessage)
    abstract fun onClearQueueCommand(event: ClearQueueMessage)
    abstract fun onSeekCommand(event: SeekMessage)
}