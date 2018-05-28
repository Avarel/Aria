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
class JedisStore(private val pool: JedisPool): Store {
    companion object {
        val LOG = LoggerFactory.getLogger(JedisStore::class.java)!!
    }

    fun connected(): Boolean {
        return try {
            this["dummy"].get()
            true
        } catch (e: JedisConnectionException) {
            false
        }
    }

    override operator fun get(vararg keys: Any): StoreNode {
        return JedisStoreNode(if (keys.size == 1) keys[0].toString() else keys.joinToString(":"))
    }

    override fun shutdown() {
        FileStore.LOG.info("Jedis store connection shutting down.")
        pool.close()
    }

    inner class JedisStoreNode(key: String): AbstractStoreNode(key) {
        override fun delete() {
            FileStore.LOG.debug("Deleting \"$key\".")
            pool.resource.use { client ->
                client.del(key)
            }
        }

        override fun get(): String? {
            LOG.debug("Retrieving \"$key\".")
            pool.resource.use { client ->
                return client[key]
            }
        }

        override fun set(value: String) {
            LOG.debug("Setting \"$key\" to \"$value\".")
            pool.resource.use { client ->
                client[key] = value
            }
        }
    }
}