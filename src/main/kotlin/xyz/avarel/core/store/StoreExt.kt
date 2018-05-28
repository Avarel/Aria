package xyz.avarel.core.store

inline fun StoreNode.getOrPut(defaultValue: () -> String): String {
    val value = get()
    return if (value == null) {
        val answer = defaultValue()
        set(answer)
        answer
    } else {
        value
    }
}

inline fun StoreNode.getBooleanOrPut(defaultValue: () -> Boolean): Boolean {
    val value = get()?.toBoolean()
    return if (value == null) {
        val answer = defaultValue()
        setBoolean(answer)
        answer
    } else {
        value
    }
}

inline fun StoreNode.getIntOrPut(defaultValue: () -> Int): Int {
    val value = get()?.toIntOrNull()
    return if (value == null) {
        val answer = defaultValue()
        setInt(answer)
        answer
    } else {
        value
    }
}

inline fun StoreNode.getLongOrPut(defaultValue: () -> Long): Long {
    val value = get()?.toLongOrNull()
    return if (value == null) {
        val answer = defaultValue()
        setLong(answer)
        answer
    } else {
        value
    }
}

inline fun StoreNode.getDoubleOrPut(defaultValue: () -> Double): Double {
    val value = get()?.toDoubleOrNull()
    return if (value == null) {
        val answer = defaultValue()
        setDouble(answer)
        answer
    } else {
        value
    }
}