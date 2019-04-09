package xyz.avarel.aria.utils

import xyz.avarel.core.commands.desc
import xyz.avarel.core.commands.descBuilder
import xyz.avarel.core.commands.sendEmbed

fun requireMusicControllerMessage(context: MessageContext) {
    context.channel.sendEmbed("No Active Music Channel") {
        descBuilder {
            append("The bot is not currently connected to any voice channel.")
            appendln()
            append("Try making the bot join channel using the command `join` first.")
        }
    }.queue()
}

fun MessageContext.requirePlayingTrackMessage() {
    channel.sendEmbed("No Active Music") {
        desc { "The bot is not currently playing any music." }
    }.queue()
}

fun MessageContext.errorMessage(reason: String) {
    channel.sendEmbed("Error") {
        desc { reason }
    }.queue()
}

fun MessageContext.invalidArgumentsMessage(type: String) {
    channel.sendEmbed("Invalid ArgumentInfo") {
        desc { "Invalid $type." }
    }.queue()
}

fun progressBar(length: Int, percent: Double, prefix: String = "", suffix: String = "", on: Char = '▇', off: Char = '▁'): String {
    return progressBarTo(StringBuilder(), length, percent, prefix, suffix, on, off).toString()
}

fun <A: Appendable> progressBarTo(buffer: A, length: Int, percent: Double, prefix: String = "", suffix: String = "", on: Char = '▇', off: Char = '▁'): A {
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