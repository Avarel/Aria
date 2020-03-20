package xyz.avarel.aria.utils

class CommandDSL(val arguments: List<String>) {
    var matched = false
    val possibleArguments: MutableList<PossibleArgument> = mutableListOf()

    inline fun string(type: String = "(string)", description: String? = null, block: CommandDSL.(String) -> Unit) {
        argParse(type, description, block) { it.firstOrNull() }
    }

    inline fun integer(type: String = "(integer)", description: String? = null, block: CommandDSL.(Int) -> Unit) {
        argParse(type, description, block) { it.firstOrNull()?.toIntOrNull() }
    }

    inline fun <T> argParse(type: String, description: String?, block: CommandDSL.(T) -> Unit, extract: (List<String>) -> T?) {
        if (matched) {
            return
        }
        val value = extract(arguments) ?: return addPossibleArgument(type, description)
        matched = true

        val dsl = CommandDSL(arguments.subList(1, arguments.size))
        dsl.block(value)

        if (!dsl.matched) {
            dsl.error()
        }
    }

    fun addPossibleArgument(type: String, description: String?) {
        possibleArguments.add(PossibleArgument(type, description))
    }

    fun error() {
        println("missing $possibleArguments")
    }
}

data class PossibleArgument(val type: String, val description: String?)

inline fun dsl(arguments: List<String>, block: CommandDSL.() -> Unit) {
    val dsl = CommandDSL(arguments)

    dsl.block()

    if (!dsl.matched) {
        dsl.error()
    }
}