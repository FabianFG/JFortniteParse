package me.fungames.jfortniteparse.ue4.io.al2

import me.fungames.jfortniteparse.ue4.io.FIoContainerId
import me.fungames.jfortniteparse.ue4.reader.FArchive

@ExperimentalUnsignedTypes
class FContainerHeader {
    var containerId: FIoContainerId
    var packageCount = 0u
    var names: ByteArray
    var nameHashes: ByteArray
    var packageIds: Array<FPackageId>
    var storeEntries: ByteArray
    var culturePackageMap: FCulturePackageMap
    var packageRedirects: Array<Pair<FPackageId, FPackageId>>

    constructor(Ar: FArchive) {
        containerId = FIoContainerId(Ar)
        packageCount = Ar.readUInt32()
        names = Ar.read(Ar.read())
        nameHashes = Ar.read(Ar.read())
        packageIds = Ar.readTArray { FPackageId(Ar) }
        storeEntries = Ar.read(Ar.read())
        culturePackageMap = Ar.readTMap { Ar.readString() to Ar.readTArray { FPackageId(Ar) to FPackageId(Ar) } }
        packageRedirects = Ar.readTArray { FPackageId(Ar) to FPackageId(Ar) }
    }
}