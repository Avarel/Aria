package xyz.avarel.aria.utils

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.music.MusicController
import xyz.avarel.core.commands.desc
import xyz.avarel.core.commands.sendEmbed

inline fun CommandDSL.requireMusic(block: CommandDSL.(controller: MusicController) -> Unit) {
    val controller = ctx.bot.musicManager.getExisting(ctx.guild.idLong)
    matched = true
    if (controller == null) {
        requireMusicControllerMessage(ctx)
    } else {
        CommandDSL(ctx, index).successOrYell { block(controller) }
    }
}

inline fun CommandDSL.requireTrack(
    controller: MusicController,
    block: CommandDSL.(track: AudioTrack) -> Unit
) {
    val track = controller.player.playingTrack
    matched = true
    if (track == null) {
        requirePlayingTrackMessage(ctx)
    } else {
        CommandDSL(ctx, index).successOrYell { block(track) }
    }
}

fun requireMusicControllerMessage(context: MessageContext) {
    context.channel.sendEmbed("Not Connected") {
        desc { "The bot is not connected to any voice channel. Try `+join` or `+play`." }
    }.queue()
}

fun requirePlayingTrackMessage(context: MessageContext) {
    context.channel.sendEmbed("Not Playing Music") {
        desc { "The bot is not playing any music." }
    }.queue()
}

fun progressBar(
    length: Int,
    percent: Double,
    prefix: String = "",
    suffix: String = "",
    on: Char = '▇',
    off: Char = '▁'
): String {
    return progressBarTo(
        StringBuilder(),
        length,
        percent,
        prefix,
        suffix,
        on,
        off
    ).toString()
}

fun <A : Appendable> progressBarTo(
    buffer: A,
    length: Int,
    percent: Double,
    prefix: String = "",
    suffix: String = "",
    on: Char = '▇',
    off: Char = '▁'
): A {
    buffer.append(prefix)
    repeat((percent * length).toInt()) {
        buffer.append(on)
    }
    repeat(length - (percent * length).toInt()) {
        buffer.append(off)
    }
    buffer.append(suffix)
    return buffer
}