package me.fungames.jfortniteparse.ue4.asyncloading2

class FEventLoadNode2 {
    fun dependsOn(other: FEventLoadNode2) {}
    fun addBarrier() {}
    fun addBarrier(count: Int) {}
    fun releaseBarrier() {}
    //fun execute(threadState: FAsyncLoadingThreadState2) {}

    //private fun processDependencies(threadState: FAsyncLoadingThreadState2) {}
    private fun fire() {}
}