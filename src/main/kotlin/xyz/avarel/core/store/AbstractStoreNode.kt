package xyz.avarel.core.store

abstract class AbstractStoreNode(protected val key: String): StoreNode {
    override fun get() = getOrNull()!!
    override fun getBoolean() = get().toBoolean()
    override fun getInt() = get().toInt()
    override fun getLong() = get().toLong()
    override fun getDouble() = get().toDouble()

    override fun getBooleanOrNull() = getOrNull()?.toBoolean()
    override fun getIntOrNull() = getOrNull()?.toIntOrNull()
    override fun getLongOrNull() = getOrNull()?.toLongOrNull()
    override fun getDoubleOrNull() = getOrNull()?.toDoubleOrNull()

    override fun setBoolean(value: Boolean) {
        set(value.toString())
    }

    override fun setInt(value: Int) {
        set(value.toString())
    }

    override fun setLong(value: Long) {
        set(value.toString())
    }

    override fun setDouble(value: Double) {
        set(value.toString())
    }
}