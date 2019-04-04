package xyz.avarel.aria.utils

import java.lang.RuntimeException
import java.time.Duration

/*

argParse(arg) {
    matchInt { i ->
    } otherwise matchBoolean { b ->
    } otherwise noMore { error() }
}

 */

class ExpectArgumentException(val info: ArgumentInfo) : RuntimeException()

class ExpectArgumentParser(ctx: MessageContext) : ArgumentParser(ctx) {
    fun expectString(type: String = "(text)", consumeRemaining: Boolean = false): String {
        return when {
            !hasNext() -> expectError(type, false)
            consumeRemaining -> ctx.arguments.subList(index, ctx.arguments.size).joinToString(" ").also { index = ctx.arguments.size }
            else -> ctx.arguments[index++]
        }
    }

    fun expectInt(): Int {
        return parseExpect("(number)", String::toIntOrNull)
    }

    fun intBetween(low: Int, high: Int): Int {
        return parseExpect("(number $low..$high)") { it.toIntOrNull()?.takeIf { num -> num in low..high } }
    }

    fun expectDouble(): Double {
        return parseExpect("(decimal)", String::toDoubleOrNull)
    }

    fun doubleBetween(low: Double, high: Double): Double {
        return parseExpect("(decimal $low..$high)") { it.toDoubleOrNull()?.takeIf { num -> num in low..high } }
    }

    fun expectDuration(): Duration {
        return parseExpect("([[hh:]mm:]ss)", String::toDurationOrNull)
    }

    inline fun <reified T: Enum<T>> expectEnum(
            name: String = T::class.java.simpleName,
            valueNames: String = enumValues<T>().joinToString(", ", "(", ")") { it.name.toLowerCase() }
    ): T {
        val typeName = "$name: $valueNames"
        val string = expectString(typeName)
        return try {
            enumValueOf(string.toUpperCase())
        } catch (e: IllegalArgumentException) {
            expectError(typeName, true)
        }
    }

    /**
     * [block] should return null if it failed to parse into type.
     */
    private inline fun <T> parseExpect(typeName: String, block: (String) -> T?): T {
        val string = expectString(typeName)
        return block(string) ?: expectError(typeName, true)
    }

    fun expectError(type: String, decreaseIndex: Boolean): Nothing {
        if (decreaseIndex) index--
        throw ExpectArgumentException(ArgumentInfo(type, null))
    }

}