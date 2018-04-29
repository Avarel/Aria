package xyz.avarel.aria

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.bot.sharding.ShardManager
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.utils.SessionControllerAdapter
import org.slf4j.LoggerFactory
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.exceptions.JedisConnectionException
import xyz.avarel.aria.commands.*
import xyz.avarel.core.commands.impl.DefaultCommandRegistry
import xyz.avarel.aria.listener.MessageContextProducer
import xyz.avarel.aria.listener.VoiceListener
import xyz.avarel.aria.music.MusicManager
import xyz.avarel.core.commands.Command
import xyz.avarel.core.commands.Dispatcher
import xyz.avarel.core.db.FileStore
import xyz.avarel.core.db.JedisStore
import xyz.avarel.core.db.Store
import java.io.File
import java.util.*

fun main(args: Array<String>) {
    val properties = Properties()
    val file = File("bot.properties")
    file.inputStream().use(properties::load)

    val token = properties.getProperty("token") ?: throw IllegalStateException("Token was not provided")
    val prefix = properties.getProperty("prefix") ?: "+"

    Bot(token, prefix)
}

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

    val store: Store = try {
        JedisStore(JedisPool(JedisPoolConfig(), "localhost")).also { it["dummy"].getOrNull() }
    } catch (e: JedisConnectionException) {
        FileStore(File("store.properties"))
    }

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
        register(FilterCommand())
    }

    init {
        LOG.info("${commandRegistry.entries.size} commands registered.")

        val dispatcher = Dispatcher(commandRegistry)

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

            setGameProvider { _ -> Game.playing("Hello there!") }

            addEventListeners(ctxProducer, VoiceListener(this@Bot))
        }.build()

        LOG.info("Building bot.")

        musicManager = MusicManager(this)
    }
}