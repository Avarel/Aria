package xyz.avarel.aria

import java.io.File
import java.util.*

fun main() {
    val properties = Properties()
    val file = File("bot.properties")
    file.inputStream().use(properties::load)

    val token = properties.getProperty("token") ?: throw IllegalStateException("Token was not provided")
    val prefix = properties.getProperty("prefix") ?: "+"

    Bot(token, prefix)
}



