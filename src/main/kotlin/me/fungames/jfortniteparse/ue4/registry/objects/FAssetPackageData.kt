package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.core.serialization.FCustomVersion
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FArchive

class FAssetPackageData {
    var packageName: FName
    var packageGuid: FGuid
    var cookedHash: FMD5Hash? = null
    var importedClasses: Array<FName>? = null
    var diskSize: Long
    var fileVersionUE = -1
    var fileVersionLicenseeUE = -1
    var customVersions: Array<FCustomVersion>? = null
    var flags = 0u

    constructor(Ar: FArchive, version: FAssetRegistryVersion) {
        packageName = Ar.readFName()
        diskSize = Ar.readInt64()
        packageGuid = FGuid(Ar)
        if (version >= FAssetRegistryVersion.Type.AddedCookedMD5Hash) {
            cookedHash = FMD5Hash(Ar)
        }
        if (version >= FAssetRegistryVersion.Type.WorkspaceDomain) {
            fileVersionUE = Ar.readInt32()
            fileVersionLicenseeUE = Ar.readInt32()
            flags = Ar.readUInt32()
            customVersions = Ar.readTArray { FCustomVersion(Ar) }
        }
        if (version >= FAssetRegistryVersion.Type.PackageImportedClasses) {
            importedClasses = Ar.readTArray { Ar.readFName() }
        }
    }
}