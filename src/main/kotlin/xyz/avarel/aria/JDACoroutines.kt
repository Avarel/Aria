package xyz.avarel.aria

import net.dv8tion.jda.core.requests.RestAction
import kotlin.coroutines.experimental.suspendCoroutine

suspend fun <T> RestAction<T>.await(): T {
    return suspendCoroutine { this.queue(it::resume, it::resumeWithException) }
}