package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.util.compareExchange
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.jvm.internal.Ref.IntRef

class FEventLoadNode2 {
    private var dependents = emptyArray<FEventLoadNode2?>()
    private var dependenciesCount = 0
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
        val expected = IntRef()
        while (!other.dependencyWriterCount.compareExchange(expected, 1)) {
            check(expected.element == 1)
            expected.element = 0
        }
        if (!other.bDone.get()) {
            barrierCount.getAndIncrement()
            if (other.dependenciesCount == 0) {
                other.dependents = arrayOf(this)
                other.dependenciesCount = 1
            } else {
                if (other.dependenciesCount == 1) {
                    val firstDependency = other.dependents[0]
                    val newDependenciesCapacity = 4
                    other.dependents = arrayOfNulls(newDependenciesCapacity)
                    other.dependents[0] = firstDependency
                } else if (other.dependenciesCount == other.dependents.size) {
                    val originalDependents = other.dependents
                    val oldDependenciesCapacity = other.dependents.size
                    val newDependenciesCapacity = oldDependenciesCapacity * 2
                    other.dependents = arrayOfNulls(newDependenciesCapacity)
                    System.arraycopy(originalDependents, 0, other.dependents, 0, oldDependenciesCapacity)
                }
                other.dependents[other.dependenciesCount++] = this
            }
        }
        other.dependencyWriterCount.set(0)
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

    private fun processDependencies(threadState: FAsyncLoadingThreadState2) {
        if (dependencyWriterCount.get() != 0) {
            do {
                Thread.sleep(0)
            } while (dependencyWriterCount.get() != 0)
        }

        if (dependents.size == 1) {
            val singleDependent = dependents[0]!!
            check(singleDependent.barrierCount.get() > 0)
            if (singleDependent.barrierCount.decrementAndGet() == 0) {
                threadState.nodesToFire.add(singleDependent)
            }
        } else if (dependents.isNotEmpty()) {
            for (dependent in dependents) {
                check(dependent != null && dependent.barrierCount.get() > 0)
                if (dependent.barrierCount.decrementAndGet() == 0) {
                    threadState.nodesToFire.add(dependent)
                }
            }
            //threadState.deferredFreeArcs.add(dependents)
        }
        if (threadState.bShouldFireNodes) {
            threadState.bShouldFireNodes = false
            while (threadState.nodesToFire.size > 0) {
                threadState.nodesToFire.removeLast().fire()
            }
            threadState.bShouldFireNodes = true
        }
    }

    private fun fire() {
        val threadState = FAsyncLoadingThreadState2.get()
        if (spec.bExecuteImmediately && threadState != null && threadState.currentEventNode == null) {
            execute(threadState)
        } else {
            spec.eventQueue.push(this)
        }
    }
}