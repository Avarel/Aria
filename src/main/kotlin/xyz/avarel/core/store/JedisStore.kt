package xyz.avarel.core.store

import org.slf4j.LoggerFactory
import redis.clients.jedis.JedisPool
import redis.clients.jedis.exceptions.JedisConnectionException

/**
 * [Store] implementation using Jedis, an client interface for
 * the Redis key-value store.
 *
 * @author Avarel
 */
class JedisStore(private val pool: JedisPool) : Store {
    private val log = LoggerFactory.getLogger(JedisStore::class.java)!!

    fun connected(): Boolean {
        return try {
            this["dummy"].get()
            true
        } catch (e: JedisConnectionException) {
            false
        }
    }

    override operator fun get(vararg keys: Any): StoreNode {
        return JedisStoreNode(
            if (keys.size == 1) keys[0].toString() else keys.joinToString(
                ":"
            )
        )
    }

    override fun shutdown() {
        log.info("Jedis store connection shutting down.")
        pool.close()
    }

    inner class JedisStoreNode(key: String) : AbstractStoreNode(key) {
        override fun delete() {
            log.debug("Deleting \"$key\".")
            pool.resource.use { client ->
                client.del(key)
            }
        }

        override fun get(): String? {
            log.debug("Retrieving \"$key\".")
            return pool.resource.use { client ->
                client[key]
            }
        }

        override fun set(value: String) {
            log.debug("Setting \"$key\" to \"$value\".")
            pool.resource.use { client ->
                client[key] = value
            }
        }
    }
}