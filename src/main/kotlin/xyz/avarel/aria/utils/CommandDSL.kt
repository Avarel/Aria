package xyz.avarel.aria.utils

import xyz.avarel.aria.ArgumentError
import xyz.avarel.aria.MessageContext
import xyz.avarel.core.commands.descBuilder
import xyz.avarel.core.commands.fieldBuilder
import xyz.avarel.core.commands.sendEmbed
import xyz.avarel.core.commands.title
import javax.annotation.CheckReturnValue
import kotlin.properties.ObservableProperty

/*

argParse(arg) {
    matchInt { i ->
    } otherwise matchBoolean { b ->
    } otherwise noMore { error() }
}

 */

private typealias DSLBlock<T> = CommandDSL.(T) -> MatchEnd

class CommandDSL(private val ctx: MessageContext) {
    var index = 0

    val currentArguments = mutableListOf<String>()
    val possibleArguments = mutableListOf<ArgumentInfo>()

    fun hasNext() = index < ctx.arguments.size

    fun peekString(): String? {
        return when {
            hasNext() -> ctx.arguments[index]
            else -> null
        }
    }

    fun nextString(): String {
        return ctx.arguments[index++]
    }

    inline fun matchString(type: String = "(text)", description: String? = null, block: DSLBlock<String>): MatchResult {
        if (!hasNext()) {
            possibleArguments += ArgumentInfo(type, description)
            return MatchResult(false)
        }

        val value = nextString()
        currentArguments += value
        block(value)

        return MatchResult(true)
    }

    inline fun matchLabel(label: String, description: String? = null, block: CommandDSL.() -> MatchEnd): MatchResult {
        if (!hasNext() || !peekString().equals(label, ignoreCase = true)) {
            possibleArguments += ArgumentInfo(label, description)
            return MatchResult(false)
        }

        index++
        currentArguments += label
        block()

        return MatchResult(true)
    }

    inline fun matchInt(type: String = "(number)", description: String? = null, block: DSLBlock<Int>): MatchResult {
        return parseMatch(type, description, String::toIntOrNull, block)
    }

    inline fun <T> parseMatch(type: String, description: String? = null, parse: (String) -> T?, block: DSLBlock<T>): MatchResult {
        val string = peekString()
        val value = string?.let(parse)

        if (value == null) {
            possibleArguments += ArgumentInfo(type, description)
            return MatchResult(false)
        }

        index++
        currentArguments += string
        block(value)
        possibleArguments.clear()

        return MatchResult(true)
    }

    inline fun <T> MatchResult.otherwiseParseMatch(type: String, description: String? = null, parse: (String) -> T?, block: DSLBlock<T>): MatchResult {
        return when {
            result -> this // which is true
            else -> parseMatch(type, description, parse, block)
        }
    }

    inline fun MatchResult.otherwiseNoMore(block: CommandDSL.() -> MatchEnd): MatchEnd {
        if (!result) {
            if (hasNext()) return otherwiseError()
            block()
        }
        return MatchEnd
    }

    fun MatchResult.otherwiseError(): MatchEnd {
        if (!result) {
            ctx.channel.sendEmbed {
                title {
                    if (hasNext()) {
                        "Invalid Argument"
                    } else {
                        "Insufficient Argument"
                    }
                }

                descBuilder {
                    append("```")
                    appendln()
                    append(ctx.label)
                    currentArguments.forEach {
                        append(' ')
                        append(it)
                    }

                    if (hasNext()) {
                        append(' ')
                        append(peekString())
                    } else {
                        append(" ...")
                    }

                    appendln()
                    val index = ctx.label.length + currentArguments.sumBy { it.length + 1 } + 1
                    repeat(index) { append(' ') }
                    if (hasNext()) {
                        val actual = peekString()!!
                        when (actual.length) {
                            0, 1 -> append("|")
                            else -> {
                                append('└')
                                repeat(actual.length - 2) {
                                    append('─')
                                }
                                append("┘")
                            }
                        }
                    } else {
                        append("└─┘")
                    }
                    append("```")
                }

                fieldBuilder("Possible Arguments") {
                    possibleArguments.forEach {
                        append("`")
                        append(it.type)
                        append('`')
                        it.description?.let { desc ->
                            append(" • ")
                            append(desc)
                        }
                    }
                }
            }.queue()
        }
        return MatchEnd
    }

    fun end() = MatchEnd
}

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
@CheckReturnValue
inline class MatchResult(val result: Boolean)
object MatchEnd

data class ArgumentInfo(val type: String, val description: String?)

inline fun argParse(ctx: MessageContext, block: CommandDSL.() -> MatchEnd) {
    CommandDSL(ctx).block()
}