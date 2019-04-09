package xyz.avarel.core.commands

/**
 * Default Command class.
 *
 * @param T
 *        Context type.
 */
abstract class AnnotatedCommand<in T>: Command<T> {
    private val annotation get() = javaClass.getAnnotation(CommandInfo::class.java)
    override val aliases = annotation.aliases
    override val title = annotation.title
    override val description = annotation.description
    override val usage = annotation.usage
    override val visible = annotation.visible
}