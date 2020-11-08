package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.ue4.assets.Package

class FLoadedPackageRef {
    var `package`: Package? = null
        set(value) {
            field = value
            bHasFailed = false
        }
    private var refCount = 0
    private var bAreAllPublicExportsLoaded = false
    private var bIsMissing = false
    private var bHasFailed = false
    private var bHasBeenLoadedDebug = false

    fun areAllPublicExportsLoaded() = bAreAllPublicExportsLoaded

    fun setAllPublicExportsLoaded() {
        check(!bIsMissing)
        check(!bHasFailed)
        check(`package` != null)
        bIsMissing = false
        bAreAllPublicExportsLoaded = true
        bHasBeenLoadedDebug = true
    }

    fun clearAllPublicExportsLoaded() {
        check(!bIsMissing)
        check(`package` != null)
        bIsMissing = false
        bAreAllPublicExportsLoaded = false
    }

    fun isMissingPackage() = bIsMissing

    fun setIsMissingPackage() {
        check(!bAreAllPublicExportsLoaded)
        check(`package` == null)
        bIsMissing = true
        bAreAllPublicExportsLoaded = false
    }

    fun clearIsMissingPackage() {
        check(!bAreAllPublicExportsLoaded)
        check(`package` == null)
        bIsMissing = false
        bAreAllPublicExportsLoaded = false
    }

    fun setHasFailed() {
        bHasFailed = true
    }
}