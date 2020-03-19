package xyz.avarel.core.commands

data class CommandInfo(
        val title: String,
        val description: String?,
        val usages: List<ArgumentInfo>,
        val visible: Boolean
)

sealed class ArgumentInfo(val label: String) {
    class Label(name: String): ArgumentInfo(name)
    class Optional(target: ArgumentInfo): ArgumentInfo("[$target]")
    class Required(target: ArgumentInfo): ArgumentInfo("($target)")
    class Options(types: List<ArgumentInfo>): ArgumentInfo(types.joinToString("|"))
    class Multi(types: List<ArgumentInfo>): ArgumentInfo(types.joinToString(" "))
    class Desc(string: String, target: ArgumentInfo): ArgumentInfo("...")

    override fun toString() = label
}

class ArgumentBuilder {
    val list: MutableList<ArgumentInfo> = mutableListOf()

    fun number() = label("number")
    fun percentage() = label("percentage")
    fun text() = label("text")
    fun range(low: Int, high: Int) = label("$low..$high")
    fun url() = label("url")
    fun timestamp() = label("[[hh]:mm]:ss")

    fun label(name: String) {
        list += ArgumentInfo.Label(name)
    }

    inline fun desc(string: String, block: ArgumentBuilder.() -> Unit) {
        list += ArgumentInfo.Desc(string, ArgumentBuilder().apply(block).build())
    }

    inline fun optional(block: ArgumentBuilder.() -> Unit) {
        list += ArgumentInfo.Optional(ArgumentBuilder().apply(block).build())
    }

    inline fun options(block: ArgumentBuilder.() -> Unit) {
        list += ArgumentBuilder().apply(block).build(options = true)
    }

    inline fun <reified T : Enum<T>> options() {
        options {
            enumValues<T>().map{ it.name.toLowerCase() }.forEach(this::label)
        }
    }

    inline fun required(block: ArgumentBuilder.() -> Unit) {
        list += ArgumentInfo.Required(ArgumentBuilder().apply(block).build())
    }

    fun build(options: Boolean = false): ArgumentInfo {
        return when {
            list.isEmpty() -> throw IllegalStateException("Usage with zero usages")
            list.size == 1 -> list[0]
            else -> if (options) ArgumentInfo.Options(list) else ArgumentInfo.Multi(list)
        }
    }
}

class CommandInfoBuilder(private val title: String) {
    var description: String? = null
    var visible: Boolean = true
    val usages: MutableList<ArgumentInfo> = mutableListOf()

    inline fun desc(block: () -> String) {
        description = block()
    }

    inline fun visible(block: () -> Boolean) {
        visible = block()
    }

    inline fun usage(block: ArgumentBuilder.() -> Unit) {
        usages += ArgumentBuilder().apply(block).build()
    }

    fun build(): CommandInfo {
        return CommandInfo(title, description, usages, visible)
    }
}


fun info(title: String, block: CommandInfoBuilder.() -> Unit): CommandInfo {
    return CommandInfoBuilder(title).apply(block).build()
}