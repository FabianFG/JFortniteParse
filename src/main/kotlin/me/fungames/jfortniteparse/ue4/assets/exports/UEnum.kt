package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

open class UEnum : UObject() {
    /** List of pairs of all enum names and values. */
    lateinit var names: Array<Pair<FName, Long>>

    /** How the enum was originally defined. */
    lateinit var cppForm: ECppForm

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        names = Ar.readTArray { Ar.readFName() to Ar.readInt64() }
        cppForm = ECppForm.values()[Ar.read()]
    }

    /** How this enum is declared in C++, affects the internal naming of enum values */
    enum class ECppForm {
        Regular,
        Namespaced,
        EnumClass
    }
}