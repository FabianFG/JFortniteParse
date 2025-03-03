package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.core.serialization.FCustomVersion
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.registry.reader.FAssetRegistryArchive
import me.fungames.jfortniteparse.ue4.versions.FPackageFileVersion

class FAssetPackageData {
    var packageName: FName
    var packageGuid: FGuid
    var cookedHash: FMD5Hash? = null
    var importedClasses: Array<FName>? = null
    var diskSize: Long
    var fileVersionUE: FPackageFileVersion
    var fileVersionLicenseeUE = -1
    var customVersions: Array<FCustomVersion>? = null
    var flags = 0u
    var extensionText: String? = null

    constructor(Ar: FAssetRegistryArchive) {
        val version = Ar.version
        packageName = Ar.readFName()
        diskSize = Ar.readInt64()
        packageGuid = FGuid(Ar)
        if (version >= FAssetRegistryVersion.Type.AddedCookedMD5Hash) {
            cookedHash = FMD5Hash(Ar)
        }
        if (version >= FAssetRegistryVersion.Type.AddedChunkHashes) {
            //chunkHashes = Ar.readTMap { FIoChunkId(Ar) to Ar.read(20) }
            Ar.skip((Ar.readInt32() * (12 + 20)).toLong())
        }
        if (version >= FAssetRegistryVersion.Type.WorkspaceDomain) {
            fileVersionUE = if (version >= FAssetRegistryVersion.Type.PackageFileSummaryVersionChange) {
                FPackageFileVersion(Ar)
            } else {
                val ue4Version = Ar.readInt32()
                FPackageFileVersion.createUE4Version(ue4Version)
            }
            fileVersionLicenseeUE = Ar.readInt32()
            flags = Ar.readUInt32()
            customVersions = Ar.readTArray { FCustomVersion(Ar) }
        } else {
            fileVersionUE = FPackageFileVersion(0, 0)
        }
        if (version >= FAssetRegistryVersion.Type.PackageImportedClasses) {
            importedClasses = Ar.readTArray { Ar.readFName() }
        }
        if (Ar.header.version >= FAssetRegistryVersion.Type.AssetPackageDataHasExtension) {
            extensionText = Ar.readString()
        }
    }
}