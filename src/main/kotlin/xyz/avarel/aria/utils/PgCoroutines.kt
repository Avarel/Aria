package xyz.avarel.aria.utils

import io.reactiverse.pgclient.PgPool
import io.reactiverse.pgclient.PgRowSet
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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