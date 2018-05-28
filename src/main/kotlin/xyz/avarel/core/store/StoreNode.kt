package xyz.avarel.core.store

/**
 * Interact with a store entry.
 */
interface StoreNode {
    /**
     * @return The possibly null string value of this node.
     */
    fun get(): String?

    /**
     * @return The possibly null boolean value of this node.
     */
    fun getBoolean(): Boolean?

    /**
     * @return The possibly null integer value of this node.
     * @throws NumberFormatException if the value is not a valid number.
     */
    fun getInt(): Int?

    /**
     * @return The possibly null long value of this node.
     * @throws NumberFormatException if the value is not a valid number.
     */
    fun getLong(): Long?

    /**
     * @return The possibly null double value of this node.
     * @throws NumberFormatException if the value is not a valid number.
     */
    fun getDouble(): Double?

    /**
     * Set the value of this node.
     * @param value String value.
     */
    fun set(value: String)

    /**
     * Set the value of this node.
     * @param value String value.
     */
    fun setBoolean(value: Boolean)

    /**
     * Set the value of this node.
     * @param value Integer value.
     */
    fun setInt(value: Int)

    /**
     * Set the value of this node.
     * @param value Long value.
     */
    fun setLong(value: Long)

    /**
     * Set the value of this node.
     * @param value Double value.
     */
    fun setDouble(value: Double)

    /**
     * Delete this node.
     */
    fun delete()
}