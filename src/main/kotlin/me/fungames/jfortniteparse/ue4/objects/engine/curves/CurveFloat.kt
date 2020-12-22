package me.fungames.jfortniteparse.ue4.objects.engine.curves

import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.assets.exports.UObject

@UStruct
class FRuntimeFloatCurve {
    @JvmField
    var EditorCurveData: FRichCurve? = null
    @JvmField
    var ExternalCurve: Lazy<UCurveFloat>? = null
}

class UCurveFloat : UObject/*UCurveBase*/() {
    /** Keyframe data */
    @JvmField
    var FloatCurve: FRichCurve? = null

    /** Flag to represent event curve */
    @JvmField
    var bIsEventCurve: Boolean? = null
}