package xyz.avarel.core.commands

annotation class CommandInfo(
        val aliases: Array<String>,
        val title: String = "No title.",
        val description: String = "No description.",
        val usage: String = "",
        val visible: Boolean = true
)