package xyz.avarel.core.store

/**
 * A key-value store abstraction.
 *
 * @see StoreNode
 */
interface Store {
    /**
     * Get a node in order to interact with the store.
     *
     * @param keys Key to the node. Multiple keys are joined
     *             by a separator which is decided by the
     *             implementation.
     */
    operator fun get(vararg keys: Any): StoreNode

    /**
     * Shutdown and cleanup the store.
     */
    fun shutdown()
}

