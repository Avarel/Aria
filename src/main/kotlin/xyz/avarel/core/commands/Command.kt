package xyz.avarel.core.commands

/**
 * Default Command class.
 *
 * @param T
 *        Context type.
 */
interface Command<in T> {
    val info: CommandInfo get() = javaClass.getAnnotation(CommandInfo::class.java)

    /**
     * The implementation of what happens when the command
     * is invoked. This command is meant to be invoked
     * using co-routines.
     */
    suspend operator fun invoke(context: T)
}