package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.ue4.objects.uobject.FName

class FAsyncPackageDesc2 {
    /** A unique request id for each external call to LoadPackage */
    var requestID: Int

    /** The package store entry with meta data about the actual disk package */
    var storeEntry: FPackageStoreEntry?

    /**
     * The disk package id corresponding to the StoreEntry.
     * It is used by the loader for io chunks and to handle ref tracking of loaded packages and import objects.
     */
    var diskPackageId: FPackageId

    /**
     * The custom package id is only set for temp packages with a valid but "fake" CustomPackageName,
     * if set, it will be used as key when tracking active async packages in AsyncPackageLookup
     */
    var customPackageId: FPackageId

    /**
     * The disk package name from the LoadPackage call, or none for imported packages
     * up until the package summary has been serialized
     */
    var diskPackageName: FName

    /**
     * The custom package name from the LoadPackage call is only used for temp packages,
     * if set, it will be used as the runtime UPackage name
     */
    var customPackageName: FName

    /** Set from the package summary */
    var sourcePackageName = FName.NAME_None

    ///** Delegate called on completion of loading. This delegate can only be created and consumed on the game thread */
    //var packageLoadedDelegate: FLoadPackageAsyncDelegate? = null

    constructor(
        requestID: Int,
        packageIdToLoad: FPackageId,
        storeEntry: FPackageStoreEntry?,
        diskPackageName: FName = FName(),
        packageId: FPackageId = FPackageId(),
        customName: FName = FName(),
        //completionDelegate: FLoadPackageAsyncDelegate
    ) {
        this.requestID = requestID
        this.storeEntry = storeEntry
        this.diskPackageId = packageIdToLoad
        this.customPackageId = packageId
        this.diskPackageName = diskPackageName
        this.customPackageName = customName
        //this.packageLoadedDelegate = completionDelegate
    }

    /** This constructor does not modify the package loaded delegate as this is not safe outside the game thread */
    constructor(oldPackage: FAsyncPackageDesc2) {
        requestID = oldPackage.requestID
        storeEntry = oldPackage.storeEntry
        diskPackageId = oldPackage.diskPackageId
        customPackageId = oldPackage.customPackageId
        diskPackageName = oldPackage.diskPackageName
        customPackageName = oldPackage.customPackageName
        sourcePackageName = oldPackage.sourcePackageName
    }

    ///** This constructor will explicitly copy the package loaded delegate and invalidate the old one */
    /*constructor(oldPackage: FAsyncPackageDesc2, packageLoadedDelegate: FLoadPackageAsyncDelegate) : this(oldPackage) {
        this.packageLoadedDelegate = packageLoadedDelegate
    }*/

    fun setDiskPackageName(serializedDiskPackageName: FName, serializedSourcePackageName: FName = FName()) {
        check(diskPackageName.isNone() || diskPackageName == serializedDiskPackageName)
        check(sourcePackageName.isNone() || sourcePackageName == serializedSourcePackageName)
        diskPackageName = serializedDiskPackageName
        sourcePackageName = serializedSourcePackageName
    }

    fun canBeImported() = customPackageName.isNone()

    /**
     * The UPackage name is used by the engine and game code for in-memory and network communication.
     */
    fun getUPackageName() = when {
        !customPackageName.isNone() -> customPackageName // temp packages
        !sourcePackageName.isNone() -> sourcePackageName // localized packages
        else -> diskPackageName // normal packages
    }

    /**
     * The AsyncPackage id is used by the loader as a key in AsyncPackageLookup to track active load requests,
     * which in turn is used for looking up packages for setting up serialized arcs (mostly post load dependencies).
     */
    inline fun getAsyncPackageId() = if (customPackageId.isValid()) customPackageId else diskPackageId
}