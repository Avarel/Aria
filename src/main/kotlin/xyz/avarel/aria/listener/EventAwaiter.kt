package xyz.avarel.aria.listener

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.hooks.EventListener
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.suspendCoroutine

class EventAwaiter : EventListener {
    private val map: MutableMap<Class<*>, MutableSet<AwaitingPoint<Event>>> = ConcurrentHashMap()

    override fun onEvent(event: Event) {
        map[event.javaClass]?.forEach { point ->
            if (point.predicate(event)) {
                point.cont.resume(Unit)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <T : Event> wait(cls: Class<T>, predicate: (T) -> Boolean) {
        return suspendCoroutine { cont ->
            map.getOrPut(cls) { ConcurrentHashMap.newKeySet() } += AwaitingPoint(predicate, cont) as AwaitingPoint<Event>
        }
    }

    suspend inline fun <reified T : Event> wait(noinline predicate: (T) -> Boolean) {
        return wait(T::class.java, predicate)
    }

    class AwaitingPoint<in T : Event>(val predicate: (T) -> Boolean, val cont: Continuation<Unit>)
}