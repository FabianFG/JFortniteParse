package me.fungames.jfortniteparse.ue4.asyncloading2

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class FEventLoadNode2 {
    private val barrierCount: AtomicInteger
    private val dependencyWriterCount = AtomicInteger()
    private val bDone = AtomicBoolean()

    private val spec: FAsyncLoadEventSpec
    private val pkg: FAsyncPackage2
    private val importOrExportIndex: Int

    constructor(spec: FAsyncLoadEventSpec, pkg: FAsyncPackage2, importOrExportIndex: Int, barrierCount: Int) {
        this.barrierCount = AtomicInteger(barrierCount)
        this.spec = spec
        this.pkg = pkg
        this.importOrExportIndex = importOrExportIndex
    }

    fun dependsOn(other: FEventLoadNode2) {
    }

    fun addBarrier() {
        barrierCount.getAndIncrement()
    }

    fun addBarrier(count: Int) {
        barrierCount.getAndAdd(count)
    }

    fun releaseBarrier() {
        check(barrierCount.get() > 0)
        if (barrierCount.decrementAndGet() == 0) {
            fire()
        }
    }

    fun execute(threadState: FAsyncLoadingThreadState2) {
        check(barrierCount.get() == 0)
        check(threadState.currentEventNode == null || threadState.currentEventNode == this)

        threadState.currentEventNode = this
        val state = spec.func(pkg, importOrExportIndex)
        if (state == EAsyncPackageState.Complete) {
            threadState.currentEventNode = null
            bDone.set(true)
            processDependencies(threadState)
        }
    }

    fun isDone() = bDone.get()

    private fun processDependencies(threadState: FAsyncLoadingThreadState2) {}

    private fun fire() {
        val threadState = FAsyncLoadingThreadState2.get()
        if (spec.bExecuteImmediately && threadState != null && threadState.currentEventNode == null) {
            execute(threadState)
        } else {
            spec.eventQueue.push(this)
        }
    }
}