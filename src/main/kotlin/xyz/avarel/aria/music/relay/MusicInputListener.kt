//package xyz.avarel.aria.music.relay
//
//abstract class MusicInputListener {
////    fun query(event: MusicQuery<*>): MusicOutput {
////        return when (event) {
////            is ChannelQuery -> onChannelQuery(event)
////            is VolumeQuery -> onVolumeQuery(event)
////            is RepeatModeQuery -> onRepeatModeQuery(event)
////            is PlaybackQuery -> onPlaybackQuery(event)
////            is QueueQuery -> onQueueQuery(event)
////        }
////    }
////
////    fun input(event: MusicInput<*>): MusicOutput {
////        return when (event) {
////            is ChannelJoinInput -> onChannelJoinInput(event)
////            is ChannelLeaveInput -> onChannelLeaveInput(event)
////            is ChannelMoveInput -> onChannelMoveInput(event)
////            is PlayInput -> onPlayInput(event)
////            is VolumeChangeInput -> onVolumeChangeInput(event)
////            is RepeatModeChangeInput -> onRepeatModeChangeInput(event)
////            is PlaybackChangeInput -> onPlaybackChangeInput(event)
////            is PlaybackChangePositionInput -> onPlaybackChangePositionInput(event)
////            is QueueRemoveInput -> onQueueRemoveInput(event)
////            is QueueClearInput -> onQueueClearInput(event)
////        }
////    }
//
//    abstract fun onChannelJoinInput(event: ChannelJoinInput): MusicResult<ChannelJoinOutput>
//
//    abstract fun onChannelQuery(event: ChannelQuery): MusicResult<ChannelOutput>
//
//    abstract fun onVolumeQuery(event: VolumeQuery): MusicResult<VolumeOutput>
//
//    abstract fun onRepeatModeQuery(event: RepeatModeQuery): MusicResult<RepeatModeOutput>
//
//    abstract fun onPlaybackQuery(event: PlaybackQuery): MusicResult<PlaybackOutput>
//
//    abstract fun onQueueQuery(event: QueueQuery): MusicResult<QueueOutput>
//
//    abstract fun onChannelLeaveInput(event: ChannelLeaveInput): MusicResult<ChannelLeaveOutput>
//
//    abstract fun onChannelMoveInput(event: ChannelMoveInput): MusicResult<ChannelMoveOutput>
//
//    abstract fun onPlayInput(event: PlayInput): MusicResult<PlayOutput>
//
//    abstract fun onVolumeChangeInput(event: VolumeChangeInput): MusicResult<VolumeChangeOutput>
//
//    abstract fun onRepeatModeChangeInput(event: RepeatModeChangeInput): MusicResult<RepeatModeChangeOutput>
//
//    abstract fun onPlaybackChangeInput(event: PlaybackChangeInput): MusicResult<PlaybackOutput>
//
//    abstract fun onPlaybackChangePositionInput(event: PlaybackChangePositionInput): MusicResult<PlaybackChangePositionOutput>
//
//    abstract fun onQueueRemoveInput(event: QueueRemoveInput): MusicResult<QueueRemoveOutput>
//
//    abstract fun onQueueClearInput(event: QueueClearInput): MusicResult<QueueClearOutput>
//}
