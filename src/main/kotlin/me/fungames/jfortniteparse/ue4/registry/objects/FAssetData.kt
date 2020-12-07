package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.ue4.registry.reader.FNameTableArchive

class FAssetData(Ar: FNameTableArchive) {
    // Serialize out the asset info
    val objectPath = Ar.readFName()
    val packagePath = Ar.readFName()
    val assetClass = Ar.readFName()

    // These are derived from ObjectPath, we manually serialize them because they get pooled
    val packageName = Ar.readFName()
    val assetName = Ar.readFName()

    // This is actually a FAssetDataTagMapSharedView which just contains a FAssetDataTagMap
    // which is just a TSortedMap<FName, FString>
    val tagsAndValues = Ar.readTMap { Ar.readFName() to Ar.readString() }
    val chunkIDs = Ar.readTArray { it.readInt32() }
    val packageFlags = Ar.readUInt32()
}