package xyz.avarel.aria.listener

import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.hooks.EventListener
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EventAwaiter : EventListener {
    private val map: MutableMap<Class<*>, MutableSet<AwaitingPoint<Event>>> = ConcurrentHashMap()

    override fun onEvent(event: Event) {
        map[event.javaClass]?.forEach { point ->
            if (point.predicate(event)) {
                point.cont.resume(event)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <T : Event> wait(cls: Class<T>, predicate: (T) -> Boolean): T {
        return suspendCoroutine { cont ->
            map.getOrPut(cls) { ConcurrentHashMap.newKeySet() } += AwaitingPoint(predicate, cont) as AwaitingPoint<Event>
        }
    }

    suspend inline fun <reified T : Event> wait(noinline predicate: (T) -> Boolean): T {
        return wait(T::class.java, predicate)
    }

    class AwaitingPoint<in T : Event>(val predicate: (T) -> Boolean, val cont: Continuation<T>)
}