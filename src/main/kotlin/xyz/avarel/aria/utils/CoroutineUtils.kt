package xyz.avarel.aria.utils

import io.reactiverse.pgclient.PgPool
import io.reactiverse.pgclient.PgRowSet
import net.dv8tion.jda.core.requests.RestAction
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> RestAction<T>.await(): T {
    return suspendCoroutine { this.queue(it::resume, it::resumeWithException) }
}

suspend fun PgPool.queryAsync(sql: String): PgRowSet {
    return suspendCoroutine { c ->
        query(sql) {
            if (it.succeeded()) {
                c.resume(it.result())
            } else {
                c.resumeWithException(it.cause())
            }
        }
        this.close()
    }
}

suspend fun Call.await(): String {
    return suspendCoroutine { cont ->
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                cont.resume(response.body()!!.string())
            }

            override fun onFailure(call: Call, e: IOException) {
                cont.resumeWithException(e)
            }
        })
    }
}