package xyz.avarel.aria

import xyz.avarel.aria.utils.toDurationOrNull
import java.time.Duration
import java.util.regex.Pattern

private val rangePattern = Pattern.compile("(\\d+)?\\s*?(?:\\.\\.|-)\\s*(\\d+)?")

class ArgumentHandler(private val list: List<String>) {
    var index = 0

    fun hasNext() = index < list.size

    fun string(type: String = "String", consumeRemaining: Boolean = false): String {
        return when {
            !hasNext() -> throw ArgumentError.Insufficient(index, type)
            consumeRemaining -> list.subList(index, list.size).joinToString(" ").also { index = list.size }
            else -> list[index++]
        }
    }

    fun number(): Int {
        return parse("Number", String::toIntOrNull)
    }

    inline fun matchNumber(block: (Int) -> Unit) {
        generalMatch("Number", String::toIntOrNull, block)
    }

    fun numberRange(low: Int, high: Int): Int {
        return parse("Number ($low..$high)") { it.toIntOrNull()?.takeIf { num -> num in low..high } }
    }

    inline fun matchNumberRange(low: Int, high: Int, block: (Int) -> Unit) {
        generalMatch("Number ($low..$high)", { it.toIntOrNull()?.takeIf { num -> num in low..high } }, block)
    }

    fun decimal(): Double {
        return parse("Decimal", String::toDoubleOrNull)
    }

    fun decimalRange(low: Double, high: Double): Double {
        return parse("Decimal ($low..$high)") { it.toDoubleOrNull()?.takeIf { num -> num in low..high } }
    }

    inline fun matchDecimalRange(low: Double, high: Double, block: (Double) -> Unit) {
        generalMatch("Decimal ($low..$high)", { it.toDoubleOrNull()?.takeIf { num -> num in low..high } }, block)
    }

    fun duration(): Duration {
        return parse("Timestamp [[hh:]mm:]ss", String::toDurationOrNull)
    }

    inline fun matchDuration(block: (Duration) -> Unit) {
        generalMatch("Timestamp [[hh:]mm:]ss", String::toDurationOrNull, block)
    }

    /**
     * [block] should return null if it failed to parse into type.
     */
    private inline fun <T> parse(typeName: String, block: (String) -> T?): T {
        val string = string(typeName)
        return block(string) ?: throw ArgumentError.Illegal(--index, typeName, string)
    }

    inline fun <T> generalMatch(typeName: String, parser: (String) -> T?, block: (T) -> Unit) {
        if (!hasNext()) return
        val string = string(typeName)
        val item = parser(string)
        if (item == null) {
            index--
        } else {
            block(item)
        }
    }

    inline fun <reified T: Enum<T>> enum(
            name: String = T::class.java.simpleName,
            valueNames: String = enumValues<T>().joinToString(", ", "(", ")") { it.name.toLowerCase() }
    ): T {
        val typeName = "$name: $valueNames"
        val string = string(typeName)
        try {
            return enumValueOf(string.toUpperCase())
        } catch (e: IllegalArgumentException) {
            throw ArgumentError.Illegal(index--, typeName, string)
        }
    }

    inline fun <reified T: Enum<T>> matchEnum(
            name: String = T::class.java.simpleName,
            valueNames: String = enumValues<T>().joinToString(", ", "(", ")") { it.name.toLowerCase() },
            block: (T) -> Unit
    ) {
        if (!hasNext()) return
        val typeName = "$name: $valueNames"
        val string = string(typeName)
        try {
            block(enumValueOf(string.toUpperCase()))
        } catch (e: IllegalArgumentException) {
            return
        }
    }

    /**
     * Optional as in if there's not a next argument, it's still fine.
     * Otherwise this will still throw an exception if it returns null
     */
    inline fun <T> optional(block: ArgumentHandler.() -> T): T? {
        return if (!hasNext()) null
        else block()
    }

    fun nextIs(vararg strings: String): Boolean {
        val typeName = strings.joinToString(", ")
        val string = string(typeName)
        return strings.any { it == string }.also { if (!it) index-- }
    }
}

sealed class ArgumentError(val position: Int): RuntimeException() {
    class Insufficient(position: Int, val type: String): ArgumentError(position)
    class Illegal(position: Int, val type: String, val actual: String): ArgumentError(position)
}