package me.fungames.jfortniteparse.ue4.asyncloading2

enum class EAsyncPackageState {
    /** Package tick has timed out. */
    TimeOut,
    /** Package has pending import packages that need to be streamed in. */
    PendingImports,
    /** Package has finished loading. */
    Complete
}