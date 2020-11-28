package me.fungames.jfortniteparse.ue4.objects.engine.curves

import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

@UStruct
class FRuntimeFloatCurve {
    @JvmField
    var EditorCurveData: FRichCurve? = null
    @JvmField
    var ExternalCurve: FPackageIndex? = null /*CurveFloat*/
}

class UCurveFloat : UObject/*UCurveBase*/() {
    /** Keyframe data */
    @JvmField
    var FloatCurve: FRichCurve? = null

    /** Flag to represent event curve */
    @JvmField
    var bIsEventCurve: Boolean? = null
}