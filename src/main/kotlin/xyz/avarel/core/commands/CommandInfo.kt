package xyz.avarel.core.commands

import java.lang.IllegalStateException

class CommandInfo(
        val title: String,
        val description: Description,
        val usages: List<Argument>
)

class Description(val text: String, vararg val expanded: String)

sealed class Argument(val label: String) {
    class Label(name: String): Argument(name)
    class Optional(target: Argument): Argument("[$target]")
    class Required(target: Argument): Argument("($target)")
    class Options(types: List<Argument>): Argument(types.joinToString("|"))
    class Multi(types: List<Argument>): Argument(types.joinToString(" "))

    override fun toString() = label
}

class ArgumentBuilder {
    val list: MutableList<Argument> = mutableListOf()

    fun number() = label("number")
    fun percentage() = label("percentage")
    fun text() = label("text")
    fun range(low: Int, high: Int) = label("$low..$high")
    fun url() = label("url")
    fun timestamp() = label("[[hh]:mm]:ss")

    fun label(name: String) {
        list += Argument.Label(name)
    }

    inline fun optional(block: ArgumentBuilder.() -> Unit) {
        list += Argument.Optional(ArgumentBuilder().apply(block).build())
    }

    inline fun options(block: ArgumentBuilder.() -> Unit) {
        list += Argument.Optional(ArgumentBuilder().apply(block).build(options = true))
    }

    inline fun required(block: ArgumentBuilder.() -> Unit) {
        list += Argument.Required(ArgumentBuilder().apply(block).build())
    }

    fun build(options: Boolean = false): Argument {
        return when {
            list.isEmpty() -> throw IllegalStateException("Usage with zero usages")
            list.size == 1 -> list[0]
            else -> if (options) Argument.Options(list) else Argument.Multi(list)
        }
    }
}

class CommandInfoBuilder(val title: String) {
    var description: String = "No desc"
    val usages: MutableList<Argument> = mutableListOf()

    inline fun desc(desc: () -> String) {
        description = desc()
    }

    inline fun usage(block: ArgumentBuilder.() -> Unit) {
        usages += ArgumentBuilder().apply(block).build()
    }

    fun build(): CommandInfo {
        return CommandInfo(title, Description(description), usages)
    }
}


fun info(title: String, block: CommandInfoBuilder.() -> Unit): CommandInfo {
    return CommandInfoBuilder(title).apply(block).build()
}