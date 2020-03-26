package xyz.avarel.aria.utils

import xyz.avarel.aria.MessageContext
import xyz.avarel.core.commands.*
import java.time.Duration

class CommandDSL(val ctx: MessageContext, val index: Int = 0) {
    private inline val arguments get() = ctx.arguments
    var matched = false

    private val possibleArguments: MutableList<PossibleArgument> =
        mutableListOf()

    private data class PossibleArgument(
        val type: String,
        val description: String?
    )

    fun hasNext() = index < arguments.size

    fun stringOrNull(consume: Boolean = false): String? {
        return when {
            !hasNext() -> null
            consume -> arguments.subList(index, arguments.size)
                .joinToString(" ")
            else -> arguments[index]
        }
    }

    inline fun string(
        type: String = "(string)",
        desc: String? = null,
        consume: Boolean = false,
        block: CommandDSL.(String) -> Unit
    ) {
        argParse(type, desc, block, consume) { it }
    }

    inline fun match(
        strings: Array<String>,
        desc: String? = null,
        block: CommandDSL.(String) -> Unit
    ) {
        argParse(strings.joinToString(" | ", "(", ")"), desc, block) { s ->
            if (strings.any { it == s }) s else null
        }
    }

    inline fun integer(
        type: String = "(integer)",
        desc: String? = null,
        block: CommandDSL.(Int) -> Unit
    ) {
        argParse(type, desc, block, extract = String::toIntOrNull)
    }

    inline fun intInRange(
        low: Int,
        high: Int,
        desc: String? = null,
        block: CommandDSL.(Int) -> Unit
    ) {
        argParse("($low..$high)", desc, block) {
            it.toIntOrNull().takeIf { i -> i in low..high }
        }
    }

    inline fun time(
        type: String = "([[hh:]mm:]ss)",
        desc: String? = null,
        block: CommandDSL.(Duration) -> Unit
    ) {
        argParse(type, desc, block, extract = String::toTimeOrNull)
    }

    inline fun <reified T : Enum<T>> enum(
        desc: String? = null,
        block: CommandDSL.(T) -> Unit
    ) {
        val type = enumValues<T>().joinToString(
            ", ",
            "(",
            ")"
        ) { it.name.toLowerCase() }
        argParse(type, desc, block) {
            try {
                enumValueOf<T>(it.toUpperCase())
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    inline fun nothing(desc: String? = null, block: CommandDSL.() -> Unit) {
        if (matched) return
        if (hasNext()) return addPossibleArgument("<no argument>", desc)
        CommandDSL(ctx, index).successOrYell(block)
        matched = true
    }

    inline fun more(block: CommandDSL.() -> Unit) {
        if (matched || !hasNext()) return
        CommandDSL(ctx, index).successOrYell(block)
        matched = true
    }

    inline fun <T> argParse(
        type: String,
        description: String?,
        block: CommandDSL.(T) -> Unit,
        consume: Boolean = false,
        extract: (String) -> T?
    ) {
        if (matched) return
        val value =
            stringOrNull(consume)?.let(extract) ?: return addPossibleArgument(
                type,
                description
            )
        CommandDSL(ctx, index + 1).successOrYell { block(value) }
        matched = true
    }

    fun addPossibleArgument(type: String, description: String?) {
        possibleArguments.add(PossibleArgument(type, description))
    }

    fun printNoMatchError() {
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

                repeat(
                    ctx.label.length + arguments.subList(0, index)
                        .sumBy { it.length + 1 } + 1) {
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

    inline fun successOrYell(block: CommandDSL.() -> Unit) {
        block()
        if (!matched) printNoMatchError()
    }
}


inline fun MessageContext.dsl(block: CommandDSL.() -> Unit) {
    CommandDSL(this).successOrYell { block() }
}