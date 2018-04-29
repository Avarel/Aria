package xyz.avarel.core.commands

/**
 * Used in conjunction with the [AnnotatedCommand] interface.
 * @see AnnotatedCommand
 */
annotation class CommandInfo (
        val aliases: Array<String>,
        val description: String = "No desc.",
        val usage: String = ""
)