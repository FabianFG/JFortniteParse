package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.util.await
import me.fungames.jfortniteparse.util.compareExchange
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference
import kotlin.jvm.internal.Ref.ObjectRef

// from Serialization/Zenaphore.h

class FZenaphoreWaiterNode {
    var next: FZenaphoreWaiterNode? = null
    var bTriggered = false
}

class FZenaphoreWaiter(private val outer: FZenaphore, private val waitCpuScopeName: String) {
    private var waiterNode = FZenaphoreWaiterNode()
    private var spinCount = 0

    fun destroy() {
        if (spinCount > 0) {
            waitInternal()
        }
    }

    fun wait0() {
        if (spinCount == 0) {
            val oldHeadWaiter = ObjectRef<FZenaphoreWaiterNode?>()
            waiterNode.bTriggered = false
            waiterNode.next = null
            while (!outer.headWaiter.compareExchange(oldHeadWaiter, waiterNode)) {
                waiterNode.next = oldHeadWaiter.element
            }
            ++spinCount
        } else {
            waitInternal()
            spinCount = 0
        }
    }

    private fun waitInternal() {
        while (true) {
            outer.event.await()
            synchronized(outer.mutex) {
                if (waiterNode.bTriggered) {
                    outer.event = CompletableFuture() // reset
                    return
                }
            }
        }
    }
}

class FZenaphore {
    internal var event = CompletableFuture<Void>()
    internal val mutex = Object()
    internal val headWaiter = AtomicReference<FZenaphoreWaiterNode?>()

    fun notifyOne() {
        while (true) {
            val waiter = ObjectRef<FZenaphoreWaiterNode?>().apply { element = headWaiter.get() ?: return }
            if (headWaiter.compareExchange(waiter, waiter.element!!.next)) {
                notifyInternal(waiter.element!!)
                return
            }
        }
    }

    fun notifyAll0() {
        val waiter = ObjectRef<FZenaphoreWaiterNode?>().apply { element = headWaiter.get() }
        while (waiter.element != null) {
            if (headWaiter.compareExchange(waiter, waiter.element!!.next)) {
                notifyInternal(waiter.element!!)
            }
        }
    }

    private fun notifyInternal(waiter: FZenaphoreWaiterNode) {
        synchronized(mutex) {
            waiter.bTriggered = true
            event.complete(null)
        }
    }
}