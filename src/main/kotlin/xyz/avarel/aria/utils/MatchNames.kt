package xyz.avarel.aria.utils

object MatchNames {
    const val INT = "(number)"
    const val DOUBLE = "(decimal)"
    const val RANGE = "(low..high)"
    const val STRING = "(text)"
    const val DURATION = "([[hh:]mm:]ss)"
    const val GENERIC_RANGE = "(range: low..high)"

    inline fun <reified T: Enum<T>> enumName(): String = T::class.java.simpleName
    inline fun <reified T: Enum<T>> enumDesc(): String = enumValues<T>().joinToString(", ")
    fun labels(strings: Array<out String>): String = strings.joinToString(" | ", "(", ")")
}