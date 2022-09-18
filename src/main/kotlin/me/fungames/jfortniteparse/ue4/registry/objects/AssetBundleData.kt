package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath
import me.fungames.jfortniteparse.ue4.objects.uobject.FTopLevelAssetPath
import me.fungames.jfortniteparse.ue4.registry.reader.FAssetRegistryArchive

/** A struct representing a single AssetBundle */
class FAssetBundleEntry(
    /** Specific name of this bundle, should be unique for a given scope */
    val bundleName: FName,

    /** List of string assets contained in this bundle */
    val bundleAssets: Array<FSoftObjectPath>
) {
    constructor(Ar: FAssetRegistryArchive) : this(Ar.readFName(),
        if (Ar.version >= FAssetRegistryVersion.Type.RemoveAssetPathFNames) {
            Ar.readTArray { FSoftObjectPath(FName(FTopLevelAssetPath(Ar).toString()), Ar.readString()) }
        } else {
            Ar.readTArray { FSoftObjectPath(Ar.readFName(), Ar.readString()) }
        }
    )
}

/** A struct with a list of asset bundle entries. If one of these is inside a UObject it will get automatically exported as the asset registry tag AssetBundleData */
class FAssetBundleData(
    /** List of bundles defined */
    val bundles: Array<FAssetBundleEntry>
) {
    constructor(Ar: FAssetRegistryArchive) : this(Ar.readTArray { FAssetBundleEntry(Ar) })
}