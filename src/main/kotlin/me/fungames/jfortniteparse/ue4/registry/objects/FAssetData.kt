package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FTopLevelAssetPath
import me.fungames.jfortniteparse.ue4.registry.reader.FAssetRegistryArchive

class FAssetData {
    val packagePath: FName
    val assetClass: FName
    val packageName: FName
    val assetName: FName
    lateinit var tagsAndValues: Map<FName, String>
    lateinit var taggedAssetBundles: FAssetBundleData
    val chunkIDs: Array<Int>
    val packageFlags: UInt

    constructor(Ar: FAssetRegistryArchive) {
        // Serialize out the asset info
        if (Ar.version < FAssetRegistryVersion.Type.RemoveAssetPathFNames) {
            val oldObjectPath = Ar.readFName()
        }
        packagePath = Ar.readFName()
        assetClass = if (Ar.version >= FAssetRegistryVersion.Type.ClassPaths) FTopLevelAssetPath(Ar).assetName else Ar.readFName()

        packageName = Ar.readFName()
        assetName = Ar.readFName()

        Ar.serializeTagsAndBundles(this)

        chunkIDs = Ar.readTArray { Ar.readInt32() }
        packageFlags = Ar.readUInt32()
    }

    val objectPath get() = "$packageName.$assetName"
}