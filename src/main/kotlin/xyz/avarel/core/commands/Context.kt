package xyz.avarel.core.commands

/**
 * General context class implementation.
 */
interface Context {
    val label: String
    val arguments: List<String>
}