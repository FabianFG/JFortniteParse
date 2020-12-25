package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.objects.FPropertyTag
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.uobject.EObjectFlags
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.deserializeUnversionedProperties

enum class EUserDefinedStructureStatus {
    /** Struct is in an unknown state. */
    UDSS_UpToDate,
    /** Struct has been modified but not recompiled. */
    UDSS_Dirty,
    /** Struct tried but failed to be compiled. */
    UDSS_Error,
    /** Struct is a duplicate, the original one was changed. */
    UDSS_Duplicate
}

class UUserDefinedStruct : UScriptStruct() {
    @JvmField var Status = EUserDefinedStructureStatus.UDSS_UpToDate
    @JvmField var Guid: FGuid? = null

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        if (hasAnyFlags(EObjectFlags.RF_ClassDefaultObject.value)) {
            return
        }
        if (false && EUserDefinedStructureStatus.UDSS_UpToDate == Status) {
            // UScriptStruct::SerializeItem
            val defaultProperties = mutableListOf<FPropertyTag>() // TODO should we save this?
            if (Ar.useUnversionedPropertySerialization) {
                deserializeUnversionedProperties(defaultProperties, this, Ar)
            } else {
                deserializeVersionedTaggedProperties(defaultProperties, Ar)
            }
        }
    }
}