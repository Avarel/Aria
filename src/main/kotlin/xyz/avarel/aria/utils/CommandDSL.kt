package xyz.avarel.aria.utils

import xyz.avarel.aria.MessageContext
import xyz.avarel.core.commands.*
import java.time.Duration

class CommandDSL(val ctx: MessageContext, val index: Int = 0) {
    inline val arguments get() = ctx.arguments
    var matched = false
    private val possibleArguments: MutableList<PossibleArgument> = mutableListOf()


    fun stringOrNull(): String? {
        return arguments.getOrNull(index)
    }

    inline fun string(type: String = "(string)", desc: String? = null, block: CommandDSL.(String) -> Unit) {
        argParse(type, desc, block) { stringOrNull() }
    }

    inline fun match(vararg strings: String, desc: String? = null, block: CommandDSL.(String) -> Unit) {
        argParse(strings.joinToString(" | ", "(", ")"), desc, block) {
            val s = stringOrNull()
            if (strings.any { it == s }) s else null
        }
    }

    inline fun integer(type: String = "(integer)", desc: String? = null, block: CommandDSL.(Int) -> Unit) {
        argParse(type, desc, block) { stringOrNull()?.toIntOrNull() }
    }

    inline fun intInRange(low: Int, high: Int, desc: String? = null, block: CommandDSL.(Int) -> Unit) {
        argParse("($low..$high)", desc, block) { stringOrNull()?.toIntOrNull() }
    }

    inline fun time(type: String = "([[hh:]mm:]ss)", desc: String? = null, block: CommandDSL.(Duration) -> Unit) {
        argParse(type, desc, block) { stringOrNull()?.toDurationOrNull() }
    }

    inline fun <reified T: Enum<T>> enum(desc: String? = null, block: CommandDSL.(T) -> Unit) {
        val type = enumValues<T>().joinToString(", ", "(", ")") { it.name.toLowerCase() }
        argParse(type, desc, block) {
            try {
                stringOrNull()?.let { enumValueOf<T>(it.toUpperCase()) }
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    inline fun default(block: CommandDSL.() -> Unit) {
        if (matched) {
            return
        }

        CommandDSL(ctx, index + 1).successOrYell(block)
    }

    inline fun ifMatched(block: CommandDSL.() -> Unit) {
        if (!matched) return
        block()
    }

    inline fun <T> argParse(type: String, description: String?, block: CommandDSL.(T) -> Unit, extract: () -> T?) {
        if (matched) {
            return
        }
        val value = extract() ?: return addPossibleArgument(type, description)
        matched = true

        CommandDSL(ctx, index + 1).successOrYell { block(value) }
    }

    fun addPossibleArgument(type: String, description: String?) {
        possibleArguments.add(PossibleArgument(type, description))
    }

    fun printPossibleArguments() {
        matched = true
        if (possibleArguments.isEmpty()) {
            return
        }
        ctx.channel.sendEmbed {
            fieldBuilder("Command Input") {
                append("```\n")
                append(ctx.label)
                arguments.forEach {
                    append(' ')
                    append(it)
                }
                appendln()

                repeat(ctx.label.length + arguments.subList(0, index).sumBy { it.length + 1 } + 1) {
                    append(' ')
                }
                when {
                    arguments.size > index -> {
                        val actual = ctx.arguments[index]
                        when (actual.length) {
                            0, 1 -> append("↑")
                            else -> {
                                append('└')
                                repeat(actual.length - 2) {
                                    append('─')
                                }
                                append("┘")
                            }
                        }
                    }
                    else -> {
                        append("└?┘")
                    }
                }
                append("```")
            }

            fieldBuilder("Expected") {
                possibleArguments.forEach {
                    append('`').append(it.type).append('`')
                    if (it.description != null) {
                        append(" - ").append(it.description)
                    }
                    appendln()
                }
            }

            when {
                arguments.size > index -> {
                    title { "Invalid Argument" }
                    desc { "⚠ One of the arguments for the command was invalid." }
                    field("Given") { ctx.arguments[index] }
                }
                else -> {
                    title { "Insufficient Argument" }
                    desc { "⚠ You need more arguments for this command." }
                }
            }
        }.queue()
    }
}

data class PossibleArgument(val type: String, val description: String?)

inline fun dsl(context: MessageContext, block: CommandDSL.() -> Unit) {
    CommandDSL(context).successOrYell { block() }
}

inline fun CommandDSL.successOrYell(block: CommandDSL.() -> Unit) {
    block()
    if (!matched) printPossibleArguments()
}