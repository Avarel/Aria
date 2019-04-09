package xyz.avarel.core.commands

/**
 * Default Command class.
 *
 * @param T
 *        Context type.
 */
interface Command<in T> {
    /**
     * Valid names that can be used to invoke the command.
     */
    val aliases: Array<String>

    val title: String get() = "No title."

    /**
     * Description of the command.
     */
    val description: String get() = "No description."

    val usage: String get() = ""

    val visible: Boolean get() = true

    /**
     * The implementation of what happens when the command
     * is invoked. This command is meant to be invoked
     * using co-routines.
     */
    suspend operator fun invoke(context: T)
}