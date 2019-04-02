package xyz.avarel.aria

import xyz.avarel.aria.utils.toDurationOrNull
import java.time.Duration
import java.util.regex.Pattern
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

private val pattern = Pattern.compile("(\\d+)?\\s*?(?:\\.\\.|-)\\s*(\\d+)?")

class ArgumentHandler(val list: List<String>) {
    var index = 0

    fun hasNext() = index < list.size

    fun string(type: String = "String", consumeRemaining: Boolean = false): String {
        return when {
            consumeRemaining -> list.subList(index, list.size).joinToString(" ")
            !hasNext() -> throw ArgumentError.Insufficient(index--, type)
            else -> list[index]
        }
    }

    fun number(): Int {
        return simple("Number", String::toIntOrNull)
    }

    fun numberRange(low: Int, high: Int): Int {
        return simple("Number ($low..$high)") { it.toIntOrNull()?.coerceIn(low, high) }
    }

    fun decimal(): Double {
        return simple("Decimal", String::toDoubleOrNull)
    }

    fun decimalRange(low: Double, high: Double): Double {
        return simple("Decimal ($low..$high)") { it.toDoubleOrNull()?.coerceIn(low, high) }
    }

    fun duration(): Duration {
        return simple("Timestamp [[hh:]mm:]ss", String::toDurationOrNull)
    }

    private inline fun <T> simple(typeName: String, block: (String) -> T?): T {
        val string = string(typeName)
        return block(string) ?: throw ArgumentError.Illegal(index--, typeName, string)
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