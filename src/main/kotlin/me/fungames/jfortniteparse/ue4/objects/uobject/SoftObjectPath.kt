package me.fungames.jfortniteparse.ue4.objects.uobject

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

/**
 * A struct that contains a string reference to an object, either a top level asset or a subobject.
 * This can be used to make soft references to assets that are loaded on demand.
 * This is stored internally as an FName pointing to the top level asset (/package/path.assetname) and an option a string subobject path.
 * If the MetaClass metadata is applied to a FProperty with this the UI will restrict to that type of asset.
 */
@ExperimentalUnsignedTypes
class FSoftObjectPath : UClass {
    /** Asset path, patch to a top level object in a package. This is /package/path.assetname */
    var assetPathName: FName

    /** Optional FString for subobject within an asset. This is the sub path after the : */
    var subPathString: String
    var owner: Package? = null

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        assetPathName = Ar.readFName()
        subPathString = Ar.readString()
        super.complete(Ar)
        owner = if (Ar.useUnversionedPropertySerialization) null else Ar.owner // TODO
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFName(assetPathName)
        Ar.writeString(subPathString)
        super.completeWrite(Ar)
    }

    constructor(assetPathName: FName, subPathString: String) {
        this.assetPathName = assetPathName
        this.subPathString = subPathString
    }

    /** Returns string representation of reference, in form /package/path.assetname[:subpath] */
    override fun toString() =
        // Most of the time there is no sub path so we can do a single string allocation
        if (subPathString.isEmpty()) {
            if (assetPathName == FName.NAME_None) "" else assetPathName.toString()
        } else {
            "$assetPathName:$subPathString"
        }

    inline fun <reified T> load() = owner?.provider?.loadObject<T>(this)
}