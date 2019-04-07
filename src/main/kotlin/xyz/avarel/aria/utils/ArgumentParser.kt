package xyz.avarel.aria.utils

import xyz.avarel.core.commands.descBuilder
import xyz.avarel.core.commands.fieldBuilder
import xyz.avarel.core.commands.sendEmbed
import xyz.avarel.core.commands.title
import java.time.Duration
import java.util.regex.Pattern

private val range = Pattern.compile("(\\d+)?\\s*?(?:\\.\\.|-)\\s*(\\d+)?")

open class ArgumentParser(val ctx: MessageContext) {
    var index = 0
    val currentArguments = mutableListOf<String>()
    val possibleArguments = mutableListOf<ArgumentInfo>()
    fun hasNext(size: Int = 1) = index + size - 1 < ctx.arguments.size

    fun peekString(): String? {
        return when {
            hasNext() -> ctx.arguments[index]
            else -> null
        }
    }

    fun peekStrings(size: Int): String? {
        return when {
            hasNext(size) -> ctx.arguments.subList(index, index + size).joinToString(" ")
            else -> null
        }
    }

    fun nextIs(vararg strings: String): Boolean {
        return nextLabel(*strings) != null
    }

    private fun nextLabel(vararg strings: String): String? {
        val peek = peekString()
        strings.forEach {
            if (it.trim().indexOf(' ') != -1) {
                val words = it.count { c -> c == ' ' }
                val peeks = peekStrings(words + 1)
                if (peeks.equals(it, ignoreCase = true)) {
                    return peeks
                }
            }
            if (peek.equals(it, ignoreCase = true)) {
                return it
            }
        }
        return null
    }

    /**
     * @return true if the next argument matches any of the [labels] and consume that argument in the process.
     */
    fun nextMatch(description: String, vararg labels: String): Boolean {
        val match = nextLabel(*labels)
        return if (match == null) {
            possibleArguments += ArgumentInfo(MatchNames.labels(labels), description)
            false
        } else {
            index += match.count { c -> c == ' ' } + 1
            true
        }
    }

    fun nextString(): String {
        return ctx.arguments[index++]
    }

    fun nextStrings(size: Int): List<String> {
        return ctx.arguments.subList(index, index + size).also { index += size }
    }

    inline fun matchString(
            description: String? = null,
            type: String = MatchNames.STRING,
            consumeRemaining: Boolean = false,
            block: ArgumentParser.(String) -> Unit
    ): Boolean {
        if (!hasNext()) {
            possibleArguments += ArgumentInfo(type, description)
            return false
        }

        val value = if (consumeRemaining) {
            val list = ctx.arguments.subList(index, ctx.arguments.size)
            index += list.size
            list.joinToString(" ")
        } else {
            nextString()
        }

        currentArguments += value
        possibleArguments.clear()
        block(value)

        return true
    }

    inline fun matchLabel(
            description: String,
            label: String,
            block: ArgumentParser.() -> Unit
    ): Boolean {
        if (!hasNext() || !nextMatch(description, label)) {
            possibleArguments += ArgumentInfo(label, description)
            return false
        }

        currentArguments += label
        possibleArguments.clear()
        block()

        return true
    }

    inline fun matchInt(
            description: String,
            type: String = MatchNames.INT,
            block: ArgumentParser.(Int) -> Unit
    ): Boolean {
        return parseMatch(description, type, String::toIntOrNull, block)
    }

    inline fun matchDouble(
            description: String,
            type: String = MatchNames.DOUBLE,
            block: ArgumentParser.(Double) -> Unit
    ): Boolean {
        return parseMatch(description, type, String::toDoubleOrNull, block)
    }

    inline fun matchDuration(
            description: String,
            type: String = MatchNames.DURATION,
            block: ArgumentParser.(Duration) -> Unit
    ): Boolean {
        return parseMatch(description, type, String::toDurationOrNull, block)
    }

    inline fun matchRange(
            description: String,
            type: String = MatchNames.GENERIC_RANGE,
            block: ArgumentParser.(IntRange) -> Unit
    ): Boolean {
        return parseMatch(description, type, String::toRangeOrNull, block)
    }

    inline fun <reified T: Enum<T>> matchEnum(
            type: String = MatchNames.enumName<T>(),
            description: String? = MatchNames.enumDesc<T>(),
            block: ArgumentParser.(T) -> Unit
    ): Boolean {
        val string = peekString()
        val value = string?.let {
            try {
                enumValueOf<T>(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        if (value == null) {
            possibleArguments += ArgumentInfo(type, description)
            return false
        }

        index++
        currentArguments += string
        possibleArguments.clear()
        block(value)

        return true
    }

    inline fun <T> parseMatch(
            description: String,
            type: String,
            parse: (String) -> T?,
            block: ArgumentParser.(T) -> Unit
    ): Boolean {
        val string = peekString()
        val value = string?.let(parse)

        if (value == null) {
            possibleArguments += ArgumentInfo(type, description)
            return false
        }

        index++
        currentArguments += string
        possibleArguments.clear()
        block(value)

        return true
    }

    fun otherwise(block: ArgumentParser.() -> Unit): Boolean {
        block()
        return true
    }

    fun matchError(): Boolean {
        ctx.channel.sendEmbed {
            title {
                if (hasNext()) {
                    "Invalid Argument"
                } else {
                    "Insufficient Arguments"
                }
            }

            descBuilder {
                append("```")
                appendln()
                append(ctx.label)
                ctx.arguments.forEach {
                    append(' ')
                    append(it)
                }

                if (hasNext()) {
                    appendln()
                    val index = ctx.label.length + currentArguments.sumBy { it.length + 1 } + 1
                    repeat(index) { append(' ') }

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
                    append(" ...")
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
                    appendln()
                }
            }
        }.queue()
        return true
    }
}

data class ArgumentInfo(val type: String, val description: String?)