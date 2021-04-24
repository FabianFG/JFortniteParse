package me.fungames.jfortniteparse.ue4.objects.engine.curves

import me.fungames.jfortniteparse.LOG_JFP
import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/** If using RCIM_Cubic, this enum describes how the tangents should be controlled in editor. */
enum class ERichCurveTangentMode {
    /** Automatically calculates tangents to create smooth curves between values. */
    RCTM_Auto,
    /** User specifies the tangent as a unified tangent where the two tangents are locked to each other, presenting a consistent curve before and after. */
    RCTM_User,
    /** User specifies the tangent as two separate broken tangents on each side of the key which can allow a sharp change in evaluation before or after. */
    RCTM_Break,
    /** No tangents. */
    RCTM_None
}


/** Enumerates tangent weight modes. */
enum class ERichCurveTangentWeightMode {
    /** Don't take tangent weights into account. */
    RCTWM_WeightedNone,
    /** Only take the arrival tangent weight into account for evaluation. */
    RCTWM_WeightedArrive,
    /** Only take the leaving tangent weight into account for evaluation. */
    RCTWM_WeightedLeave,
    /** Take both the arrival and leaving tangent weights into account for evaluation. */
    RCTWM_WeightedBoth
}

/** One key in a rich, editable float curve */
class FRichCurveKey {
    /** Interpolation mode between this key and the next */
    var interpMode: ERichCurveInterpMode

    /** Mode for tangents at this key */
    var tangentMode: ERichCurveTangentMode

    /** If either tangent at this key is 'weighted' */
    var tangentWeightMode: ERichCurveTangentWeightMode

    /** Time at this key */
    var time: Float

    /** Value at this key */
    var value: Float

    /** If RCIM_Cubic, the arriving tangent at this key */
    var arriveTangent: Float

    /** If RCTWM_WeightedArrive or RCTWM_WeightedBoth, the weight of the left tangent */
    var arriveTangentWeight: Float

    /** If RCIM_Cubic, the leaving tangent at this key */
    var leaveTangent: Float

    /** If RCTWM_WeightedLeave or RCTWM_WeightedBoth, the weight of the right tangent */
    var leaveTangentWeight: Float

    constructor(Ar: FArchive) {
        interpMode = ERichCurveInterpMode.values().getOrElse(Ar.read()) {
            LOG_JFP.warn("Unknown ERichCurveInterpMode with ordinal $it, falling back to RCIM_Linear")
            ERichCurveInterpMode.RCIM_Linear
        }
        tangentMode = ERichCurveTangentMode.values().getOrElse(Ar.read()) {
            LOG_JFP.warn("Unknown ERichCurveTangentMode with ordinal $it, falling back to RCTM_Auto")
            ERichCurveTangentMode.RCTM_Auto
        }
        tangentWeightMode = ERichCurveTangentWeightMode.values().getOrElse(Ar.read()) {
            LOG_JFP.warn("Unknown ERichCurveTangentWeightMode with ordinal $it, falling back to RCTWM_WeightedNone")
            ERichCurveTangentWeightMode.RCTWM_WeightedNone
        }
        time = Ar.readFloat32()
        value = Ar.readFloat32()
        arriveTangent = Ar.readFloat32()
        arriveTangentWeight = Ar.readFloat32()
        leaveTangent = Ar.readFloat32()
        leaveTangentWeight = Ar.readFloat32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.write(interpMode.ordinal)
        Ar.write(tangentMode.ordinal)
        Ar.write(tangentWeightMode.ordinal)
        Ar.writeFloat32(time)
        Ar.writeFloat32(value)
        Ar.writeFloat32(arriveTangent)
        Ar.writeFloat32(arriveTangentWeight)
        Ar.writeFloat32(leaveTangent)
        Ar.writeFloat32(leaveTangentWeight)
    }

    constructor(
        interpMode: ERichCurveInterpMode,
        tangentMode: ERichCurveTangentMode,
        tangentWeightMode: ERichCurveTangentWeightMode,
        time: Float,
        value: Float,
        arriveTangent: Float,
        arriveTangentWeight: Float,
        leaveTangent: Float,
        leaveTangentWeight: Float
    ) {
        this.interpMode = interpMode
        this.tangentMode = tangentMode
        this.tangentWeightMode = tangentWeightMode
        this.time = time
        this.value = value
        this.arriveTangent = arriveTangent
        this.arriveTangentWeight = arriveTangentWeight
        this.leaveTangent = leaveTangent
        this.leaveTangentWeight = leaveTangentWeight
    }
}

/** A rich, editable float curve */
@UStruct
class FRichCurve : FRealCurve() {
    /** Sorted array of keys */
    @JvmField
    @UProperty("Keys")
    var keys = mutableListOf<FRichCurveKey>()
}