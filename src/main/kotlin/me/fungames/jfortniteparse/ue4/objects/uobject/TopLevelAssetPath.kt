package me.fungames.jfortniteparse.ue4.objects.uobject

import me.fungames.jfortniteparse.ue4.objects.uobject.FName.Companion.NAME_None
import me.fungames.jfortniteparse.ue4.reader.FArchive

class FTopLevelAssetPath {
    /** Name of the package containing the asset e.g. /Path/To/Package  */
    var packageName = NAME_None
    /** Name of the asset within the package e.g. 'AssetName'  */
    var assetName = NAME_None

    constructor()

    constructor(packageName: FName, assetName: FName) {
        this.packageName = packageName
        this.assetName = assetName
    }

    constructor(Ar: FArchive) {
        packageName = Ar.readFName()
        assetName = Ar.readFName()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        if (!packageName.isNone()) {
            builder.append(packageName)
            if (!assetName.isNone()) {
                builder.append('.').append(assetName)
            }
        }
        return builder.toString()
    }
}