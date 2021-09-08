package me.fungames.jfortniteparse.ue4.objects.moviescene.channels

import me.fungames.jfortniteparse.LOG_JFP
import me.fungames.jfortniteparse.ue4.objects.core.misc.FFrameNumber
import me.fungames.jfortniteparse.ue4.objects.core.misc.FFrameRate
import me.fungames.jfortniteparse.ue4.objects.engine.curves.ERichCurveExtrapolation
import me.fungames.jfortniteparse.ue4.objects.engine.curves.ERichCurveInterpMode
import me.fungames.jfortniteparse.ue4.objects.engine.curves.ERichCurveTangentMode
import me.fungames.jfortniteparse.ue4.objects.engine.curves.ERichCurveTangentWeightMode
import me.fungames.jfortniteparse.ue4.reader.FArchive

class FMovieSceneTangentData {
    var arriveTangent: Float
    var leaveTangent: Float
    var arriveTangentWeight: Float
    var leaveTangentWeight: Float
    var tangentWeightMode: ERichCurveTangentWeightMode

    constructor(Ar: FArchive) {
        arriveTangent = Ar.readFloat32()
        leaveTangent = Ar.readFloat32()
        arriveTangentWeight = Ar.readFloat32()
        leaveTangentWeight = Ar.readFloat32()
        tangentWeightMode = ERichCurveTangentWeightMode.values().getOrElse(Ar.read()) {
            LOG_JFP.warn("Unknown ERichCurveTangentWeightMode with ordinal $it, falling back to RCTWM_WeightedNone")
            ERichCurveTangentWeightMode.RCTWM_WeightedNone
        }
        Ar.skip(3) // align from 17 to 20
    }

    constructor(arriveTangent: Float, leaveTangent: Float, arriveTangentWeight: Float, leaveTangentWeight: Float, tangentWeightMode: ERichCurveTangentWeightMode) {
        this.arriveTangent = arriveTangent
        this.leaveTangent = leaveTangent
        this.arriveTangentWeight = arriveTangentWeight
        this.leaveTangentWeight = leaveTangentWeight
        this.tangentWeightMode = tangentWeightMode
    }
}

class FMovieSceneFloatValue {
    var value: Float
    var tangent: FMovieSceneTangentData
    var interpMode: ERichCurveInterpMode
    var tangentMode: ERichCurveTangentMode

    constructor(Ar: FArchive) {
        value = Ar.readFloat32()
        tangent = FMovieSceneTangentData(Ar)
        interpMode = ERichCurveInterpMode.values().getOrElse(Ar.read()) {
            LOG_JFP.warn("Unknown ERichCurveInterpMode with ordinal $it, falling back to RCIM_None")
            ERichCurveInterpMode.RCIM_None
        }
        tangentMode = ERichCurveTangentMode.values().getOrElse(Ar.read()) {
            LOG_JFP.warn("Unknown ERichCurveTangentMode with ordinal $it, falling back to RCTM_None")
            ERichCurveTangentMode.RCTM_None
        }
        Ar.skip(2) // align from 26 to 28
    }

    constructor(value: Float, tangent: FMovieSceneTangentData, interpMode: ERichCurveInterpMode, tangentMode: ERichCurveTangentMode) {
        this.value = value
        this.tangent = tangent
        this.interpMode = interpMode
        this.tangentMode = tangentMode
    }
}

class FMovieSceneFloatChannel {
    var preInfinityExtrap: ERichCurveExtrapolation
    var postInfinityExtrap: ERichCurveExtrapolation
    var times: Array<FFrameNumber>
    var values: Array<FMovieSceneFloatValue>
    var defaultValue: Float
    var hasDefaultValue: Boolean
    var tickResolution: FFrameRate

    constructor(Ar: FArchive) {
        preInfinityExtrap = ERichCurveExtrapolation.values().getOrElse(Ar.read()) {
            LOG_JFP.warn("Unknown ERichCurveExtrapolation with ordinal $it, falling back to RCCE_None")
            ERichCurveExtrapolation.RCCE_None
        }
        postInfinityExtrap = ERichCurveExtrapolation.values().getOrElse(Ar.read()) {
            LOG_JFP.warn("Unknown ERichCurveExtrapolation with ordinal $it, falling back to RCCE_None")
            ERichCurveExtrapolation.RCCE_None
        }
        val timesSerializedElementSize = Ar.readInt32()
        times = Ar.readTArray { FFrameNumber(Ar) }
        val valuesSerializedElementSize = Ar.readInt32()
        values = Ar.readTArray { FMovieSceneFloatValue(Ar) }
        defaultValue = Ar.readFloat32()
        hasDefaultValue = Ar.readBoolean()
        tickResolution = FFrameRate(Ar.readInt32(), Ar.readInt32())
        check(Ar.readInt32() == 0) // Mysterious 4 byte padding, could this be KeyHandles which is inside if (Ar.IsTransacting())?
    }

    constructor(preInfinityExtrap: ERichCurveExtrapolation, postInfinityExtrap: ERichCurveExtrapolation, times: Array<FFrameNumber>, values: Array<FMovieSceneFloatValue>, defaultValue: Float, hasDefaultValue: Boolean, tickResolution: FFrameRate) {
        this.preInfinityExtrap = preInfinityExtrap
        this.postInfinityExtrap = postInfinityExtrap
        this.times = times
        this.values = values
        this.defaultValue = defaultValue
        this.hasDefaultValue = hasDefaultValue
        this.tickResolution = tickResolution
    }
}