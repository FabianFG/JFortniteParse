package me.fungames.jfortniteparse.ue4.io.al2

import me.fungames.jfortniteparse.ue4.reader.FArchive

@ExperimentalUnsignedTypes
class FPackageStoreEntry {
    var exportBundlesSize = 0uL
    var exportCount = 0
    var exportBundleCount = 0
    var loadOrder = 0u
    var pad = 0u
    var importedPackages: Array<FPackageId>

    constructor(Ar: FArchive) {
        exportBundlesSize = Ar.readUInt64()
        exportCount = Ar.readInt32()
        exportBundleCount = Ar.readInt32()
        loadOrder = Ar.readUInt32()
        pad = Ar.readUInt32()
        val importedPackagesInitialPos = Ar.pos()
        val importedPackagesArrayNum = Ar.readUInt32()
        val importedPackagesOffsetToDataFromThis = Ar.readUInt32()
        Ar.seek(importedPackagesInitialPos + importedPackagesOffsetToDataFromThis.toInt())
        importedPackages = Ar.readTArray(importedPackagesArrayNum.toInt()) { FPackageId(Ar) }
        Ar.seek(importedPackagesInitialPos + 4 /*arrayNum*/ + 4 /* offsetToDataFromThis*/)
    }
}