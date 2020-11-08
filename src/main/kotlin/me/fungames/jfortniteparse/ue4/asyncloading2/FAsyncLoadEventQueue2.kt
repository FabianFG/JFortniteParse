package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.util.compareExchange
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReferenceArray
import kotlin.jvm.internal.Ref.IntRef
import kotlin.jvm.internal.Ref.ObjectRef

class FAsyncLoadEventQueue2 {
    var zenaphore: FZenaphore? = null
    private val head = AtomicInteger()
    private val tail = AtomicInteger()
    private val entries = AtomicReferenceArray<FEventLoadNode2>(524288)

    fun popAndExecute(threadState: FAsyncLoadingThreadState2): Boolean {
        threadState.currentEventNode?.apply {
            check(!isDone())
            execute(threadState)
            return true
        }

        var node: FEventLoadNode2? = null
        val localHead = IntRef().apply { element = head.get() }
        val localTail = IntRef().apply { element = tail.get() }
        while (true) {
            if (localTail.element >= localHead.element) {
                break
            }
            if (tail.compareExchange(localTail, localTail.element + 1)) {
                while (node == null) {
                    node = entries.getAndSet(localTail.element % (entries.length() - 1), null)
                }
                break
            }
        }

        return if (node != null) {
            node.execute(threadState)
            true
        } else {
            false
        }
    }

    fun push(node: FEventLoadNode2) {
        val localHead = head.getAndIncrement()
        val expected = ObjectRef<FEventLoadNode2>()
        if (!entries.compareExchange(localHead % (entries.length() - 1), expected, node)) {
            // queue is full
        }
        zenaphore?.notifyOne()
    }
}