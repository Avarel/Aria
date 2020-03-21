package xyz.avarel.core.store

import org.slf4j.LoggerFactory
import java.io.File
import java.util.*
import kotlin.concurrent.thread

/**
 * Extremely basic [Store] implementation using
 * [Properties], which is backed by a [java.util.concurrent.ConcurrentHashMap].
 *
 * @param file If a file is provided, entries is saved into the file after
 *             the store shut down.
 *
 * @author Avarel
 */
class FileStore(private val file: File? = null): Store {
    val log = LoggerFactory.getLogger(FileStore::class.java)!!

    private val properties = Properties()

    init {
        if (file != null) {
            if (!file.exists()) {
                file.createNewFile()
                log.info("Created ${file.absolutePath}.")
            }
            file.inputStream().use { input ->
                properties.load(input)
                log.info("Loaded file store from ${file.absolutePath}.")
            }
        } else {
            log.warn("File store initialized without a file.")
            log.warn("All data will be lost when the program shuts down")
        }
        Runtime.getRuntime().addShutdownHook(thread(start = false, block = ::shutdown))
    }

    override operator fun get(vararg keys: Any): StoreNode {
        return FileStoreNode(if (keys.size == 1) keys[0].toString() else keys.joinToString("/"))
    }

    override fun shutdown() {
        log.info("File store shutting down.")
        if (file == null) {
            log.warn("No file was provided, all data will be lost.")
            return
        }

        file.outputStream().use { out ->
            properties.store(out, "key value store")
            log.info("Data have been saved to ${file.absolutePath}.")
        }
    }

    inner class FileStoreNode(key: String): AbstractStoreNode(key) {
        override fun delete() {
            log.debug("Deleting \"$key\".")
            properties.remove(key)
        }

        override fun get(): String? {
            log.debug("Retrieving \"$key\".")
            return properties.getProperty("key")
        }

        override fun set(value: String) {
            log.debug("Setting \"$key\" to \"$value\".")
            properties.setProperty(key, value)
        }
    }
}