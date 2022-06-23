package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FTopLevelAssetPath
import me.fungames.jfortniteparse.ue4.registry.reader.FAssetRegistryArchive

class FAssetData {
    val objectPath: FName
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
        this.objectPath = Ar.readFName()
        this.packagePath = Ar.readFName()
        this.assetClass = if (Ar.version >= FAssetRegistryVersion.Type.ClassPaths) FTopLevelAssetPath(Ar).assetName else Ar.readFName()

        // These are derived from ObjectPath, we manually serialize them because they get pooled
        this.packageName = Ar.readFName()
        this.assetName = Ar.readFName()

        Ar.serializeTagsAndBundles(this)

        this.chunkIDs = Ar.readTArray { Ar.readInt32() }
        this.packageFlags = Ar.readUInt32()
    }
}