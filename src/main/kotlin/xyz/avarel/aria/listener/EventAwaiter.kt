package xyz.avarel.aria.listener

import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EventAwaiter : EventListener {
    private val map: MutableMap<Class<*>, MutableSet<AwaitingPoint<GenericEvent>>> =
        ConcurrentHashMap()

    override fun onEvent(event: GenericEvent) {
        map[event.javaClass]?.forEach { point ->
            if (point.predicate(event)) {
                point.cont.resume(event)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <T : GenericEvent> wait(
        cls: Class<T>,
        predicate: (T) -> Boolean
    ): T {
        return suspendCoroutine { cont ->
            map.getOrPut(cls) { ConcurrentHashMap.newKeySet() }.add(
                AwaitingPoint(predicate, cont) as AwaitingPoint<GenericEvent>
            )
        }
    }

    suspend inline fun <reified T : GenericEvent> wait(noinline predicate: (T) -> Boolean): T {
        return wait(T::class.java, predicate)
    }

    class AwaitingPoint<in T : GenericEvent>(
        val predicate: (T) -> Boolean,
        val cont: Continuation<T>
    )
}