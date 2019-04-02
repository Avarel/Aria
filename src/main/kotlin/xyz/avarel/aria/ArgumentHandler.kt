package xyz.avarel.aria

import xyz.avarel.aria.utils.toDurationOrNull
import java.time.Duration
import java.util.regex.Pattern

private val pattern = Pattern.compile("(\\d+)?\\s*?(?:\\.\\.|-)\\s*(\\d+)?")

class ArgumentHandler(val list: List<String>) {
    var index = 0

    fun hasNext() = index < list.size

    fun string(type: String = "string", consumeRemaining: Boolean = false): String {
        return if (consumeRemaining) {
            list.subList(index, list.size).joinToString(" ")
        } else {
            list.getOrNull(index++) ?: throw ArgumentError.Insufficient(index--, type)
        }
    }

    fun number(): Int {
        val string = string("number")
        return string.toIntOrNull() ?: throw ArgumentError.Illegal(index--, "number", string)
    }

    fun numberRange(low: Int, high: Int): Int {
        val typeName = "number $low..$high"
        val string = string(typeName)
        return string.toIntOrNull()?.coerceIn(low, high) ?: throw ArgumentError.Illegal(index--, typeName, string)
    }

    fun decimal(): Double {
        val string = string("decimal")
        return string.toDoubleOrNull() ?: throw ArgumentError.Illegal(index--, "decimal", string)
    }

    fun decimalRange(low: Double, high: Double): Double {
        val typeName = "decimal $low..$high"
        val string = string(typeName)
        return string.toDoubleOrNull()?.coerceIn(low, high) ?: throw ArgumentError.Illegal(index--, typeName, string)
    }

    fun duration(): Duration {
        val string = string("timestamp")
        return string.toDurationOrNull() ?: throw ArgumentError.Illegal(index--, "timestamp [[hh:]mm:]ss", string)
    }

    inline fun <reified T: Enum<T>> enum(
            name: String = T::class.java.simpleName,
            valueNames: String = enumValues<T>().joinToString(", ", "(", ")") { it.name.toLowerCase() }
    ): T {
        val typeName = "$name: $valueNames"
        val string = string(typeName)
        return try {
            enumValueOf(string.toUpperCase())
        } catch (e: IllegalArgumentException) {
            throw ArgumentError.Illegal(index--, typeName, string)
        }
    }

    inline fun <T> optional(block: ArgumentHandler.() -> T): T? {
        return try {
            block()
        } catch (e: Exception) {
            null
        }
    }

    inline fun <T> match(block: ArgumentHandler.() -> T): Boolean {
        return optional(block) != null
    }

    fun match(vararg strings: String): Boolean {
        val typeName = strings.joinToString(", ")
        val string = string(typeName)
        return strings.any { it == string }.also { if (!it) index-- }
    }
}

sealed class ArgumentError(val position: Int): RuntimeException() {
    class Insufficient(position: Int, val type: String): ArgumentError(position)
    class Illegal(position: Int, val type: String, val actual: String): ArgumentError(position)
}