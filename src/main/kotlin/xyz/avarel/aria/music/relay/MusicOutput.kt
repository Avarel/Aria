package xyz.avarel.aria.music.relay

import xyz.avarel.aria.music.RepeatMode

sealed class MusicOutput

class JoinMusicChannelOutput(result: JoinChannelResult): MusicOutput()
class MoveMusicChannelOutput(result: JoinChannelResult): MusicOutput()

enum class JoinChannelResult {
    SUCCESS,
    USER_LIMIT,
    NO_PERMISSION
}

class LeaveMusicChannelOutput(successfull: Boolean): MusicOutput()
class PlayMusicOutput(result: PlayMusicResult): MusicOutput()

enum class PlayMusicResult {
    SUCCESS,
    MAX_CAPACITY,
    NOT_FOUND,
    ILLEGAL
}

class ChangeRepeatMusicOutput(oldValue: RepeatMode, newValue: RepeatMode): MusicOutput()
class ChangePlaybackMusicOutput(oldValue: Boolean, newValue: Boolean): MusicOutput()
class ChangeVolumeOutput(oldValue: Int, newValue: Int): MusicOutput()
class RemoveTrackOuput(successfull: Boolean): MusicOutput()
class RemoveTrackRangeOutput(successfull: Boolean): MusicOutput()
class ClearQueueOutput(successfull: Boolean): MusicOutput()
class SeekOutput(oldValue: Long, newValue: Long): MusicOutput()