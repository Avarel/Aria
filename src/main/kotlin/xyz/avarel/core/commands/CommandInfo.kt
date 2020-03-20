package xyz.avarel.core.commands

annotation class CommandInfo(
        val aliases: Array<String>,
        val description: String = "No description.",
        val visible: Boolean = true
)