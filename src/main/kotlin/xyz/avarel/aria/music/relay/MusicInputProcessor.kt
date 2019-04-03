package xyz.avarel.aria.music.relay

import xyz.avarel.aria.music.MusicController
import xyz.avarel.aria.music.RepeatMode

class MusicInputProcessor(private val controller: MusicController): AbstractMusicInputListener() {
    override fun onJoinMusicChannelInput(event: JoinMusicChannelInput) {
        TODO("not implemented")
    }

    override fun onLeaveMusicChannelInput(event: LeaveMusicChannelInput) {
        controller.destroy()
    }

    override fun onMoveMusicChannelInput(event: MoveMusicChannelInput) {
        TODO("not implemented")
    }

    override fun onPlayMusicInput(event: PlayMusicInput) {
        TODO("not implemented")
    }

    override fun onChangeVolumeInput(event: ChangeVolumeInput) {
        controller.player.volume = event.volume
    }

    override fun onChangeRepeatMusicInput(event: ChangeRepeatMusicInput) {
        controller.scheduler.repeatMode = enumValues<RepeatMode>()[event.repeatMode]
    }

    override fun onPlaybackMusicInput(event: PlaybackMusicInput) {
        controller.player.isPaused = event.pause
    }

    override fun onRemoveTrackInput(event: RemoveTrackInput) {
        controller.scheduler.remove(event.index)
    }

    override fun onRemoveTrackRangeInput(event: RemoveTrackRangeInput) {
        for (i in event.low..event.high) {
            controller.scheduler.remove(i)
        }
    }

    override fun onClearQueueInput(event: ClearQueueInput) {
        controller.scheduler.queue.clear()
    }

    override fun onSeekInput(event: SeekInput) {
        controller.player.playingTrack.position = event.position
    }

}