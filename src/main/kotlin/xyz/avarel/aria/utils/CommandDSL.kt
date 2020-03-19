package xyz.avarel.aria.utils

class CommandDSL(private val arguments: List<String>) {
    private var matched = false

    private inline fun string(type: String = "(string)", description: String? = null, block: CommandDSL.(string: String) -> Unit) {
        argParse(type, description, block) { arguments.firstOrNull() }
    }

    private inline fun <T> argParse(type: String, description: String?, block: CommandDSL.(T) -> Unit, extractor: () -> T?) {
        if (matched) {
            return
        }
        val value = extractor() ?: return addError(type, description)

        val dsl = CommandDSL(arguments.subList(1, arguments.size))
        dsl.block(value)

        if (!dsl.matched) {
            dsl.error()
            matched = true
        }
    }

    private fun addError(type: String, description: String?) {

    }

    private fun error() {

    }
}