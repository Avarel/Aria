package xyz.avarel.aria

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.bot.sharding.ShardManager
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.utils.SessionControllerAdapter
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import xyz.avarel.aria.commands.*
import xyz.avarel.aria.commands.music.*
import xyz.avarel.aria.listener.EventAwaiter
import xyz.avarel.aria.listener.MessageContextProducer
import xyz.avarel.aria.listener.VoiceListener
import xyz.avarel.aria.music.MusicManager
import xyz.avarel.aria.utils.MessageContext
import xyz.avarel.core.commands.Command
import xyz.avarel.core.commands.Dispatcher
import xyz.avarel.core.commands.impl.DefaultCommandRegistry
import xyz.avarel.core.store.FileStore
import xyz.avarel.core.store.Store
import java.io.File

/**
 * Main bot instance.
 *
 * @author Avarel
 */

class Bot(token: String, val prefix: String) {
    companion object {
        val LOG = LoggerFactory.getLogger(Bot::class.java)!!
    }

    val shardManager: ShardManager
    val musicManager: MusicManager

    val store: Store = FileStore(File("store.properties"))

    val waiter: EventAwaiter = EventAwaiter()

    val commandRegistry = DefaultCommandRegistry<Command<MessageContext>>().apply {
        register(InfoCommand())
        register(PingCommand())

        register(JoinCommand())
        register(LeaveCommand())
        register(PlayCommand())
        register(PauseCommand())
        register(VolumeCommand())
        register(QueueCommand())
        register(CurrentCommand())
        register(RepeatCommand())
        register(SkipCommand())
        register(SeekCommand())

        register(TestCommand())
    }

    init {
        LOG.info("${commandRegistry.entries.size} commands registered.")

        @Suppress("EXPERIMENTAL_API_USAGE")
        val dispatcher = Dispatcher(GlobalScope, commandRegistry)

        val ctxProducer = MessageContextProducer(bot = this, dispatcher = dispatcher)

        shardManager = DefaultShardManagerBuilder().apply {
            setToken(token)

            setShardsTotal(1)
            setShards(0, 0)

            setBulkDeleteSplittingEnabled(false)
            setMaxReconnectDelay(32)
            setSessionController(SessionControllerAdapter())

            // JDA-NAS does not have darwin naties.
            System.getProperty("os.name").let {
                if (it.indexOf("darwin", ignoreCase = true) < 0 && it.indexOf("mac", ignoreCase = true) < 0) {
                    setAudioSendFactory(NativeAudioSendFactory())
                }
            }

            setGameProvider { Game.playing("Hello there!") }

            addEventListeners(ctxProducer, VoiceListener(this@Bot))
        }.build()

        LOG.info("Building bot.")

        musicManager = MusicManager(this)
    }
}