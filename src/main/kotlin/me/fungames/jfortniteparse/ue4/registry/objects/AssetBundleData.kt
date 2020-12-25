package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath
import me.fungames.jfortniteparse.ue4.reader.FArchive

/** A struct representing a single AssetBundle */
class FAssetBundleEntry(
    /** Specific name of this bundle, should be unique for a given scope */
    val bundleName: FName,

    /** List of string assets contained in this bundle */
    val bundleAssets: Array<FSoftObjectPath>
) {
    constructor(Ar: FArchive) : this(Ar.readFName(), Ar.readTArray { FSoftObjectPath(Ar) })
}

/** A struct with a list of asset bundle entries. If one of these is inside a UObject it will get automatically exported as the asset registry tag AssetBundleData */
class FAssetBundleData(
    /** List of bundles defined */
    val bundles: Array<FAssetBundleEntry>
) {
    constructor(Ar: FArchive) : this(Ar.readTArray { FAssetBundleEntry(Ar) })
}