package xyz.avarel.aria.utils

import net.dv8tion.jda.api.requests.RestAction
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> RestAction<T>.await(): T {
    return suspendCoroutine { this.queue(it::resume, it::resumeWithException) }
}