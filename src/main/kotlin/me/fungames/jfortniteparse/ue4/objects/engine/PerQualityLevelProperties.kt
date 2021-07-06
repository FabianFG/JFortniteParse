package me.fungames.jfortniteparse.ue4.objects.engine

import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.reader.FArchive

@UStruct
class FPerQualityLevelInt {
    @UProperty("Default")
    var default: Int
    @UProperty("PerQuality")
    var perQuality: MutableMap<Int, Int>
    /*@UProperty("bIsEnabled")
    var isEnabled: Boolean*/

    constructor(Ar: FArchive) {
        val cooked = Ar.readBoolean()
        default = Ar.readInt32()
        perQuality = Ar.readTMap { Ar.readInt32() to Ar.readInt32() }
    }
}