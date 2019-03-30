package xyz.avarel.aria.utils

import io.reactiverse.pgclient.PgPool
import io.reactiverse.pgclient.PgRowSet
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun PgPool.queryAsync(sql: String): PgRowSet {
    return suspendCoroutine { coroutine ->
        query(sql) {
            if (it.succeeded()) {
                coroutine.resume(it.result())
            } else {
                coroutine.resumeWithException(it.cause())
            }
        }
        this.close()
    }
}