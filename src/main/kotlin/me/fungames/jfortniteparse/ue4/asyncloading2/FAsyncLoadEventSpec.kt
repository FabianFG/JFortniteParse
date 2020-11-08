package me.fungames.jfortniteparse.ue4.asyncloading2

typealias FAsyncLoadEventFunc = (FAsyncPackage2, Int) -> EAsyncPackageState

class FAsyncLoadEventSpec(
    val func: FAsyncLoadEventFunc,
    val eventQueue: FAsyncLoadEventQueue2,
    val bExecuteImmediately: Boolean = false
)