package xyz.avarel.core.commands.impl

import xyz.avarel.core.commands.Command
import xyz.avarel.core.commands.CommandRegistry

class DefaultCommandRegistry<C: Command<*>> : CommandRegistry<C> {
    private val map: MutableMap<String, C> = mutableMapOf()

    override val entries: Collection<C> get() = map.values.distinct()

    override fun register(cmd: C) {
        cmd.aliases.forEach {
            if (map[it] != null) {
                throw IllegalStateException("Duplicate command for alias $it.")
            }
            map[it] = cmd
        }
    }


    override operator fun get(alias: String): C? {
        return map[alias]
    }
}