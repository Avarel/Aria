package xyz.avarel.core.store

inline fun StoreNode.getOrPut(defaultValue: () -> String): String {
    val value = getOrNull()
    return if (value == null) {
        val answer = defaultValue()
        set(answer)
        answer
    } else {
        value
    }
}

inline fun StoreNode.getBooleanOrPut(defaultValue: () -> Boolean): Boolean {
    val value = getOrNull()?.toBoolean()
    return if (value == null) {
        val answer = defaultValue()
        setBoolean(answer)
        answer
    } else {
        value
    }
}

inline fun StoreNode.getIntOrPut(defaultValue: () -> Int): Int {
    val value = getOrNull()?.toIntOrNull()
    return if (value == null) {
        val answer = defaultValue()
        setInt(answer)
        answer
    } else {
        value
    }
}

inline fun StoreNode.getLongOrPut(defaultValue: () -> Long): Long {
    val value = getOrNull()?.toLongOrNull()
    return if (value == null) {
        val answer = defaultValue()
        setLong(answer)
        answer
    } else {
        value
    }
}

inline fun StoreNode.getDoubleOrPut(defaultValue: () -> Double): Double {
    val value = getOrNull()?.toDoubleOrNull()
    return if (value == null) {
        val answer = defaultValue()
        setDouble(answer)
        answer
    } else {
        value
    }
}