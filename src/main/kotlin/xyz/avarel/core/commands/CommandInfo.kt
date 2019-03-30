package xyz.avarel.core.commands

class CommandInfo(
        val title: String,
        val description: Description,
        vararg val usage: Usage
)

class Description(val text: String, vararg val expanded: String)

class Usage(vararg val arguments: Argument)

sealed class Argument {
    object Number: Argument()
    object Percentage: Argument()
    object Text: Argument()
    object URL: Argument()
    object Timestamp: Argument()
    class Specific(val name: String): Argument()
    class Optional(val target: Argument): Argument()
    class Required(val target: Argument): Argument()
    class List(val type: Argument): Argument()
    class Options(vararg val types: Argument): Argument()
}