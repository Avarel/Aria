
import net.dv8tion.jda.core.Permission

enum class ConnectResult {
    /**
     * Successfully connected to the voice channel.
     */
    SUCCESS,

    /**
     * Could not connect to the voice channel because of the user
     * limit and lacked the [Permission.VOICE_MOVE_OTHERS].
     */
    USER_LIMIT,

    /**
     * Insufficient permission ([Permission.VOICE_CONNECT]) to join the channel.
     */
    NO_PERMISSION
}