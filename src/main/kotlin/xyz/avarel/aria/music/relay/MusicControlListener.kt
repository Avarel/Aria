//package xyz.avarel.aria.music.relay
//
//import com.sedmelluq.discord.lavaplayer.track.AudioTrack
//import net.dv8tion.jda.core.Permission
//import org.slf4j.LoggerFactory
//import xyz.avarel.aria.music.ConnectResult
//import xyz.avarel.aria.music.MusicInstance
//
//class MusicControlListener(val instance: MusicInstance): MusicInputListener() {
//    companion object {
//        val LOG = LoggerFactory.getLogger(MusicControlListener::class.java)!!
//    }
//
//    override fun onChannelQuery(event: ChannelQuery): MusicResult<ChannelOutput> {
//        return instance.channel.idLong.let(::ChannelOutput).ok()
//    }
//
//    override fun onVolumeQuery(event: VolumeQuery): MusicResult<VolumeOutput> {
//        return instance.player.volume.let(::VolumeOutput).ok()
//    }
//
//    override fun onRepeatModeQuery(event: RepeatModeQuery): MusicResult<RepeatModeOutput> {
//        return instance.scheduler.repeatMode.let(::RepeatModeOutput).ok()
//    }
//
//    override fun onPlaybackQuery(event: PlaybackQuery): MusicResult<PlaybackOutput> {
//        return instance.player.isPaused.let(::PlaybackOutput).ok()
//    }
//
//    override fun onQueueQuery(event: QueueQuery): MusicResult<QueueOutput> {
//        return instance.scheduler.queue.map(AudioTrack::toMusicTrack).let(::QueueOutput).ok()
//    }
//
////    inline fun <reified T: MusicOutput> processInput(event: MusicInput<T>): T {
////        return input(event) as? T ?: error("Illegal state")
////    }
////
////    inline fun <reified T: MusicOutput> processQuery(event: MusicQuery<T>): T {
////        return query(event) as? T ?: error("Illegal state")
////    }
//
//    override fun onChannelJoinInput(event: ChannelJoinInput): MusicResult<ChannelJoinOutput> {
//        val guild = instance.guild
//        val channel = guild.getVoiceChannelById(event.targetChannelId)
//        MusicInstance.LOG.debug("Attempting to connect to $guild :: $channel.")
//        return when {
//            instance.destroyed -> throw IllegalStateException("Music manager is destroyed")
//            !guild.selfMember.hasPermission(channel, Permission.VOICE_CONNECT) -> {
//                MusicInstance.LOG.debug("Can't connect to $guild :: $channel because no permission.")
//                "Can't connect to the channel ${channel.name} because the bot has insufficient permission".err()
//            }
//            channel.userLimit != 0
//                    && guild.selfMember.hasPermission(channel, Permission.VOICE_MOVE_OTHERS)
//                    && channel.members.size >= channel.userLimit -> {
//                MusicInstance.LOG.debug("Can't connect to $guild :: $channel because it is full.")
//                "Can't connect to the channel ${channel.name} because the channel is full.".err()
//            }
//            else -> {
//                guild.audioManager.sendingHandler = instance.sendHandler
//                guild.audioManager.openAudioConnection(channel)
//                instance.channel = channel
//                MusicInstance.LOG.debug("Successfully connected to $guild :: $channel.")
//                ChannelJoinOutput.ok()
//            }
//        }
//    }
//
//    override fun onChannelLeaveInput(event: ChannelLeaveInput): MusicResult<ChannelLeaveOutput> {
//        instance.destroy()
//        return ChannelLeaveOutput.ok()
//    }
//
//    override fun onChannelMoveInput(event: ChannelMoveInput): MusicResult<ChannelMoveOutput> {
//        TODO("not implemented")
//    }
//
//    override fun onPlayInput(event: PlayInput): MusicResult<PlayOutput> {
//        TODO("not implemented")
//    }
//
//    override fun onVolumeChangeInput(event: VolumeChangeInput): MusicResult<VolumeChangeOutput> {
//        TODO("not implemented")
//    }
//
//    override fun onRepeatModeChangeInput(event: RepeatModeChangeInput): MusicResult<RepeatModeChangeOutput> {
//        TODO("not implemented")
//    }
//
//    override fun onPlaybackChangeInput(event: PlaybackChangeInput): MusicResult<PlaybackOutput> {
//        instance.player.playingTrack ?: return "There is no song playing right now.".err()
//
//        instance.player.isPaused = event.pause
//
//        return PlaybackOutput(instance.player.isPaused).ok()
//    }
//
//    override fun onPlaybackChangePositionInput(event: PlaybackChangePositionInput): MusicResult<PlaybackChangePositionOutput> {
//        TODO("not implemented")
//    }
//
//    override fun onQueueRemoveInput(event: QueueRemoveInput): MusicResult<QueueRemoveOutput> {
//        TODO("not implemented")
//    }
//
//    override fun onQueueClearInput(event: QueueClearInput): MusicResult<QueueClearOutput> {
//        TODO("not implemented")
//    }
//}