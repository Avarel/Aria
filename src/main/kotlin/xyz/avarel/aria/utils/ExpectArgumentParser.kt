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

class ExpectArgumentException : RuntimeException()

class ExpectArgumentParser(ctx: MessageContext) : ArgumentParser(ctx) {
    fun expectString(description: String, type: String = MatchNames.STRING, consumeRemaining: Boolean = false): String {
        matchString(description, type, consumeRemaining) { return it }
        expectError()
    }

    fun expectInt(description: String, type: String = MatchNames.INT): Int {
        matchInt(description, type) { return it }
        expectError()
    }

    fun optionalInt(): Int? {
        matchInt("") { return it }
        return null
    }

//    fun intBetween(low: Int, high: Int): Int {
//        return parseExpect("(number: $low..$high)") { it.toIntOrNull()?.takeIf { num -> num in low..high } }
//    }

    fun expectDouble(description: String, type: String = MatchNames.DOUBLE): Double {
        matchDouble(description, type) { return it }
        expectError()
    }

//    fun doubleBetween(low: Double, high: Double): Double {
//        return parseExpect("(decimal: $low..$high)") { it.toDoubleOrNull()?.takeIf { num -> num in low..high } }
//    }

    fun expectDuration(description: String, type: String = MatchNames.DURATION): Duration {
        matchDuration(description, type) { return it }
        expectError()
    }

    fun expectRange(description: String, type: String = MatchNames.GENERIC_RANGE): IntRange {
        matchRange(description, type) { return it }
        expectError()
    }

    inline fun <reified T: Enum<T>> expectEnum(
            type: String = MatchNames.enumName<T>(),
            description: String? = MatchNames.enumDesc<T>()
    ): T {
        matchEnum<T>(type, description) { return it }
        expectError()
    }

    fun expectError(): Nothing {
        throw ExpectArgumentException()
    }
}