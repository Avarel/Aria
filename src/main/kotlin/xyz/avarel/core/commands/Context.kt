package xyz.avarel.core.commands

/**
 * General context class implementation.
 */
interface Context {
    val label: String
    val args: List<String>
}