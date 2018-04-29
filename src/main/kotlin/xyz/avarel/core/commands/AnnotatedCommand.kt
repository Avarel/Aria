package xyz.avarel.core.commands

/**
 * This class will automatically set up the [Command.aliases],
 * [Command.description], and [Command.usage] by using the
 * [CommandInfo] annotation. The annotation must be used
 * for the automatic setup.
 */
interface AnnotatedCommand<in T>: Command<T> {
    private val annotation get() = javaClass.getAnnotation(CommandInfo::class.java)
    override val aliases get() = annotation.aliases
    override val description get() = annotation.description
    override val usage get() = annotation.usage
}