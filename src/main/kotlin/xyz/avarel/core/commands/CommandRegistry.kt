package xyz.avarel.core.commands

interface CommandRegistry<C : Command<*>> {
    val entries: Collection<C>

    fun register(cmd: C)
    operator fun get(alias: String): C?
}