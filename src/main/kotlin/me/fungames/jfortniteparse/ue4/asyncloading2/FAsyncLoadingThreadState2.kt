package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.io.FIoDispatcher

class FAsyncLoadingThreadState2(val ioDispatcher: FIoDispatcher) {
    val deferredFreeArcs = mutableListOf<Pair<FEventLoadNode2, UInt>>()
    val nodesToFire = mutableListOf<FEventLoadNode2>()
    var currentEventNode: FEventLoadNode2? = null
    val bShouldFireNodes = true
    var bUseTimeLimit = false
    var timeLimit = 0.0
    var startTime = 0.0
    var lastTestTime = -1.0

    fun hasDeferredFrees() = deferredFreeArcs.isNotEmpty()

    /*fun processDeferredFrees() {
        if (deferredFreeArcs.isNotEmpty()) {
        }
    }*/

    fun setTimeLimit(bInUseTimeLimit: Boolean, timeLimit: Double) {
        this.bUseTimeLimit = bInUseTimeLimit
        this.timeLimit = timeLimit
        this.startTime = (System.currentTimeMillis() / 1000).toDouble()
    }

    fun isTimeLimitExceeded(lastTypeOfWorkPerformed: String? = null, lastObjectWorkWasPerformedOn: UObject? = null): Boolean {
        var bTimeLimitExceeded = false

        if (bUseTimeLimit) {
            val currentTime = (System.currentTimeMillis() / 1000).toDouble()
            bTimeLimitExceeded = currentTime - startTime > timeLimit

            /*if (bTimeLimitExceeded && GWarnIfTimeLimitExceeded) {
                isTimeLimitExceededPrint(startTime, currentTime, lastTestTime, timeLimit, lastTypeOfWorkPerformed, lastObjectWorkWasPerformedOn)
            }*/

            lastTestTime = currentTime
        }

        /*if (!bTimeLimitExceeded) {
            bTimeLimitExceeded = isGarbageCollectionWaiting()
            if (bTimeLimitExceeded) {
                LOG_STREAMING.debug("Timing out async loading due to Garbage Collection request")
            }
        }*/

        return bTimeLimitExceeded
    }

    companion object {
        @JvmStatic
        private val THREAD_LOCAL = ThreadLocal<FAsyncLoadingThreadState2>()

        @JvmStatic
        fun create(ioDispatcher: FIoDispatcher) = FAsyncLoadingThreadState2(ioDispatcher).also { THREAD_LOCAL.set(it) }

        @JvmStatic
        fun get(): FAsyncLoadingThreadState2? = THREAD_LOCAL.get()
    }
}
