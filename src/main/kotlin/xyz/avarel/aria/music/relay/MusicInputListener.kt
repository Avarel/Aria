//package xyz.avarel.aria.music.relay
//
//abstract class MusicInputListener {
//    fun query(event: MusicQuery<*>): MusicOutput {
//        return when (event) {
//            is ChannelQuery -> onChannelQuery(event)
//            is VolumeQuery -> onVolumeQuery(event)
//            is RepeatModeQuery -> onRepeatModeQuery(event)
//            is PlaybackQuery -> onPlaybackQuery(event)
//            is QueueQuery -> onQueueQuery(event)
//        }
//    }
//
//    abstract fun onChannelQuery(event: ChannelQuery): MusicOutput
//
//    abstract fun onVolumeQuery(event: VolumeQuery): MusicOutput
//
//    abstract fun onRepeatModeQuery(event: RepeatModeQuery): MusicOutput
//
//    abstract fun onPlaybackQuery(event: PlaybackQuery): MusicOutput
//
//    abstract fun onQueueQuery(event: QueueQuery): MusicOutput
//
//    fun input(event: MusicInput<*>): MusicOutput {
//        return when (event) {
//            is ChannelJoinInput -> onChannelJoinInput(event)
//            is ChannelLeaveInput -> onChannelLeaveInput(event)
//            is ChannelMoveInput -> onChannelMoveInput(event)
//            is PlayInput -> onPlayInput(event)
//            is VolumeChangeInput -> onVolumeChangeInput(event)
//            is RepeatModeChangeInput -> onRepeatModeChangeInput(event)
//            is PlaybackChangeInput -> onPlaybackChangeInput(event)
//            is PlaybackChangePositionInput -> onPlaybackChangePositionInput(event)
//            is QueueRemoveInput -> onQueueRemoveInput(event)
//            is QueueClearInput -> onQueueClearInput(event)
//        }
//    }
//
//    abstract fun onChannelJoinInput(event: ChannelJoinInput): MusicOutput
//
//    abstract fun onChannelLeaveInput(event: ChannelLeaveInput): MusicOutput
//
//    abstract fun onChannelMoveInput(event: ChannelMoveInput): MusicOutput
//
//    abstract fun onPlayInput(event: PlayInput): MusicOutput
//
//    abstract fun onVolumeChangeInput(event: VolumeChangeInput): MusicOutput
//
//    abstract fun onRepeatModeChangeInput(event: RepeatModeChangeInput): MusicOutput
//
//    abstract fun onPlaybackChangeInput(event: PlaybackChangeInput): MusicOutput
//
//    abstract fun onPlaybackChangePositionInput(event: PlaybackChangePositionInput): MusicOutput
//
//    abstract fun onQueueRemoveInput(event: QueueRemoveInput): MusicOutput
//
//    abstract fun onQueueClearInput(event: QueueClearInput): MusicOutput
//}
