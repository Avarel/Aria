package xyz.avarel.aria.utils

class CommandDSL(private val arguments: List<String>) {
    private var matched = false

    private inline fun string(type: String = "(string)", description: String? = null, block: CommandDSL.(string: String) -> Unit) {
        if (!arguments.isEmpty()) {
            val dsl = CommandDSL(arguments.subList(1, arguments.size))
            dsl.block(arguments.first())
            if (!dsl.matched) {
                dsl.error()
            }
        } else {
            addError(type, description)
        }
    }

    private fun addError(type: String, description: String?) {

    }

    private fun error() {

    }
}