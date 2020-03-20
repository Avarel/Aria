package xyz.avarel.aria

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import kotlinx.coroutines.GlobalScope
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.SessionControllerAdapter
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.LoggerFactory
import xyz.avarel.aria.commands.*
import xyz.avarel.aria.listener.EventAwaiter
import xyz.avarel.aria.listener.InitialListener
import xyz.avarel.aria.listener.MessageContextProducer
import xyz.avarel.aria.listener.VoiceListener
import xyz.avarel.aria.music.MusicManager
import xyz.avarel.core.commands.*
import xyz.avarel.core.commands.impl.DefaultCommandRegistry
import xyz.avarel.core.store.FileStore
import xyz.avarel.core.store.Store
import java.io.File
import java.util.*

/**
 * Main bot instance.
 *
 * @author Avarel
 */

class Bot(
        token: String,
        val name: String = "Aria",
        val prefix: String = "+"
) {
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
        register(CurrentCommand())
        register(VolumeCommand())
        register(QueueCommand())
        register(ClearCommand())
        register(RemoveCommand())
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

        shardManager = DefaultShardManagerBuilder.createDefault(token).apply {
            setShardsTotal(1)
            setShards(0, 0)

            setBulkDeleteSplittingEnabled(false)
            setMaxReconnectDelay(32)
            setSessionController(SessionControllerAdapter())

            setEnabledIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES)
            setMemberCachePolicy(MemberCachePolicy.ONLINE)
            setChunkingFilter(ChunkingFilter.NONE)
            setDisabledCacheFlags(EnumSet.of(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE))

            // JDA-NAS does not have darwin naties.
            System.getProperty("os.name").let {
                if (it.indexOf("darwin", ignoreCase = true) < 0 && it.indexOf("mac", ignoreCase = true) < 0) {
                    setAudioSendFactory(NativeAudioSendFactory())
                }
            }

            addEventListeners(ctxProducer, VoiceListener(this@Bot), InitialListener(this@Bot))
            setActivity(Activity.playing("${prefix}help | I'm alive!"))
        }.build()

        LOG.info("Building bot.")

        musicManager = MusicManager(this)
    }
}