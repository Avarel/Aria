//package xyz.avarel.aria.music.relay
//
//import com.sedmelluq.discord.lavaplayer.track.AudioTrack
//import net.dv8tion.jda.core.Permission
//import org.slf4j.LoggerFactory
//import xyz.avarel.aria.music.MusicInstance
//
//class MusicControlListener(val instance: MusicInstance): MusicInputListener() {
//    override fun onChannelQuery(event: ChannelQuery): MusicOutput {
//        return instance.channel.idLong.let(::ChannelOutput)
//    }
//
//    override fun onVolumeQuery(event: VolumeQuery): MusicOutput {
//        return instance.player.volume.let(::VolumeOutput)
//    }
//
//    override fun onRepeatModeQuery(event: RepeatModeQuery): MusicOutput {
//        return instance.scheduler.repeatMode.let(::RepeatModeOutput)
//    }
//
//    override fun onPlaybackQuery(event: PlaybackQuery): MusicOutput {
//        return instance.player.isPaused.let(::PlaybackOutput)
//    }
//
//    override fun onQueueQuery(event: QueueQuery): MusicOutput {
//        return instance.scheduler.queue.map(AudioTrack::toMusicTrack).let(::QueueOutput)
//    }
//
//    companion object {
//        val LOG = LoggerFactory.getLogger(MusicControlListener::class.java)!!
//    }
//
//    inline fun <reified T: MusicOutput> processInput(event: MusicInput<T>): T {
//        return input(event) as? T ?: error("Illegal state")
//    }
//
//    inline fun <reified T: MusicOutput> processQuery(event: MusicQuery<T>): T {
//        return query(event) as? T ?: error("Illegal state")
//    }
//
//    override fun onChannelJoinInput(event: ChannelJoinInput): MusicOutput {
//        val guild = instance.guild
//        val channel = guild.getVoiceChannelById(event.targetChannelId)
//        return ChannelJoinOutput(instance.connect(channel))
//    }
//
//    override fun onChannelLeaveInput(event: ChannelLeaveInput): MusicOutput {
//        val vc = instance.channel!!
//        instance.destroy()
//        return ChannelLeaveOutput(vc.idLong)
//    }
//
//    override fun onChannelMoveInput(event: ChannelMoveInput): MusicOutput {
//        TODO("not implemented")
//    }
//
//    override fun onPlayInput(event: PlayInput): MusicOutput {
//        TODO("not implemented")
//    }
//
//    override fun onVolumeChangeInput(event: VolumeChangeInput): MusicOutput {
//        TODO("not implemented")
//    }
//
//    override fun onRepeatModeChangeInput(event: RepeatModeChangeInput): MusicOutput {
//        TODO("not implemented")
//    }
//
//    override fun onPlaybackChangeInput(event: PlaybackChangeInput): MusicOutput {
//        instance.player.playingTrack ?: return NoSongError
//
//        instance.player.isPaused = event.pause
//
//        return PlaybackOutput(instance.player.isPaused)
//    }
//
//    override fun onPlaybackChangePositionInput(event: PlaybackChangePositionInput): MusicOutput {
//        TODO("not implemented")
//    }
//
//    override fun onQueueRemoveInput(event: QueueRemoveInput): MusicOutput {
//        TODO("not implemented")
//    }
//
//    override fun onQueueClearInput(event: QueueClearInput): MusicOutput {
//        TODO("not implemented")
//    }
//}