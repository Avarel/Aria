package xyz.avarel.core.store

abstract class AbstractStoreNode(protected val key: String): StoreNode {
    override fun getBoolean() = get()?.toBoolean()
    override fun getInt() = get()?.toIntOrNull()
    override fun getLong() = get()?.toLongOrNull()
    override fun getDouble() = get()?.toDoubleOrNull()


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