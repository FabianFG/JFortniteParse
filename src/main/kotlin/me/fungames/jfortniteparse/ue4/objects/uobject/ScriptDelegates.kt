package me.fungames.jfortniteparse.ue4.objects.uobject

import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

/**
 * Script delegate base class.
 */
class FScriptDelegate {
    /** The object bound to this delegate, or null if no object is bound */
    var `object`: FPackageIndex

    /** Name of the function to call on the bound object */
    var functionName: FName

    constructor(Ar: FAssetArchive) {
        `object` = FPackageIndex(Ar)
        functionName = Ar.readFName()
    }

    constructor(`object`: FPackageIndex, functionName: FName) {
        this.`object` = `object`
        this.functionName = functionName
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        `object`.serialize(Ar)
        Ar.writeFName(functionName)
    }
}

/**
 * Script multi-cast delegate base class
 */
class FMulticastScriptDelegate {
    /** Ordered list functions to invoke when the Broadcast function is called */
    var invocationList: MutableList<FScriptDelegate>

    constructor(Ar: FAssetArchive) {
        invocationList = Ar.readArray { FScriptDelegate(Ar) }
    }

    constructor(invocationList: MutableList<FScriptDelegate>) {
        this.invocationList = invocationList
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        Ar.writeInt32(invocationList.size)
        invocationList.forEach { it.serialize(Ar) }
    }
}