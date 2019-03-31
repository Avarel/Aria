package xyz.avarel.aria.utils

import xyz.avarel.aria.MessageContext
import xyz.avarel.core.commands.desc
import xyz.avarel.core.commands.sendEmbed

fun requireMusicControllerMessage(context: MessageContext) {
    context.channel.sendEmbed("No Active Music Channel") {
        desc { "The bot is not currently connected to any voice channel." }
    }.queue()
}

fun requirePlayingTrackMessage(context: MessageContext) {
    context.channel.sendEmbed("No Active Music") {
        desc { "The bot is not currently playing any music." }
    }.queue()
}

fun insufficientArgumentsMessage(context: MessageContext, type: String) {
    context.channel.sendEmbed("Insufficient Arguments") {
        desc { "Please include $type in the usage." }
    }.queue()
}

fun errorMessage(context: MessageContext, reason: String) {
    context.channel.sendEmbed("Error") {
        desc { reason }
    }.queue()
}

fun invalidArgumentsMessage(context: MessageContext, type: String) {
    context.channel.sendEmbed("Invalid Argument") {
        desc { "Invalid $type." }
    }.queue()
}

fun progressBar(length: Int, percent: Double, prefix: String = "", suffix: String = "", on: Char = '▓', off: Char = '░'): String {
    return progressBarTo(StringBuilder(), length, percent, prefix, suffix, on, off).toString()
}

fun <A: Appendable> progressBarTo(buffer: A, length: Int, percent: Double, prefix: String = "", suffix: String = "", on: Char = '▓', off: Char = '░'): A {
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