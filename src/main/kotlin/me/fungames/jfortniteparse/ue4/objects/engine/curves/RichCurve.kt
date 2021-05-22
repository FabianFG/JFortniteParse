package me.fungames.jfortniteparse.ue4.objects.engine.curves

import me.fungames.jfortniteparse.LOG_JFP
import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.objects.core.math.isNearlyZero
import me.fungames.jfortniteparse.ue4.objects.core.math.lerp
import me.fungames.jfortniteparse.ue4.objects.engine.curves.ERichCurveExtrapolation.*
import me.fungames.jfortniteparse.ue4.objects.engine.curves.ERichCurveTangentWeightMode.*
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import java.awt.geom.CubicCurve2D.solveCubic
import kotlin.jvm.internal.Ref.FloatRef
import kotlin.jvm.internal.Ref.IntRef
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

    /** Remap inTime based on pre and post infinity extrapolation values */
    override fun remapTimeValue(inTime: FloatRef, cycleValueOffset: FloatRef) {
        val numKeys = keys.size

        if (numKeys < 2) {
            return
        }

        if (inTime.element <= keys[0].time) {
            if (preInfinityExtrap != RCCE_Linear && preInfinityExtrap != RCCE_Constant) {
                val minTime = keys[0].time
                val maxTime = keys[numKeys - 1].time

                val cycleCount = IntRef().apply { element = 0 }
                cycleTime(minTime, maxTime, inTime, cycleCount)

                if (preInfinityExtrap == RCCE_CycleWithOffset) {
                    val dv = keys[0].value - keys[numKeys - 1].value
                    cycleValueOffset.element = dv * cycleCount.element
                } else if (preInfinityExtrap == RCCE_Oscillate) {
                    if (cycleCount.element % 2 == 1) {
                        inTime.element = minTime + (maxTime - inTime.element)
                    }
                }
            }
        } else if (inTime.element >= keys[numKeys - 1].time) {
            if (postInfinityExtrap != RCCE_Linear && postInfinityExtrap != RCCE_Constant) {
                val minTime = keys[0].time
                val maxTime = keys[numKeys - 1].time

                val cycleCount = IntRef().apply { element = 0 }
                cycleTime(minTime, maxTime, inTime, cycleCount)

                if (postInfinityExtrap == RCCE_CycleWithOffset) {
                    val dv = keys[numKeys - 1].value - keys[0].value
                    cycleValueOffset.element = dv * cycleCount.element
                } else if (postInfinityExtrap == RCCE_Oscillate) {
                    if (cycleCount.element % 2 == 1) {
                        inTime.element = minTime + (maxTime - inTime.element)
                    }
                }
            }
        }
    }

    /** Evaluate this rich curve at the specified time */
    override fun eval(inTime: Float, inDefaultValue: Float): Float {
        var inTime = inTime
        // Remap time if extrapolation is present and compute offset value to use if cycling
        var cycleValueOffset = 0f
        val inTimeRef = FloatRef().apply { element = inTime }
        val cycleValueOffsetRef = FloatRef().apply { element = cycleValueOffset }
        remapTimeValue(inTimeRef, cycleValueOffsetRef)
        inTime = inTimeRef.element
        cycleValueOffset = cycleValueOffsetRef.element

        val numKeys = keys.size

        // If the default value hasn't been initialized, use the incoming default value
        var interpVal = if (defaultValue == Float.MAX_VALUE) inDefaultValue else defaultValue

        if (numKeys == 0) {
            // If no keys in curve, return the Default value.
        } else if (numKeys < 2 || (inTime <= keys[0].time)) {
            if (preInfinityExtrap == RCCE_Linear && numKeys > 1) {
                val dt = keys[1].time - keys[0].time

                if (isNearlyZero(dt)) {
                    interpVal = keys[0].value
                } else {
                    val dv = keys[1].value - keys[0].value
                    val slope = dv / dt

                    interpVal = slope * (inTime - keys[0].time) + keys[0].value
                }
            } else {
                // Otherwise if constant or in a cycle or oscillate, always use the first key value
                interpVal = keys[0].value
            }
        } else if (inTime < keys[numKeys - 1].time) {
            // perform a lower bound to get the second of the interpolation nodes
            var first = 1
            val last = numKeys - 1
            var count = last - first

            while (count > 0) {
                val step = count / 2
                val middle = first + step

                if (inTime >= keys[middle].time) {
                    first = middle + 1
                    count -= step + 1
                } else {
                    count = step
                }
            }

            interpVal = evalForTwoKeys(keys[first - 1], keys[first], inTime)
        } else {
            if (postInfinityExtrap == RCCE_Linear) {
                val dt = keys[numKeys - 2].time - keys[numKeys - 1].time

                if (isNearlyZero(dt)) {
                    interpVal = keys[numKeys - 1].value
                } else {
                    val dv = keys[numKeys - 2].value - keys[numKeys - 1].value
                    val slope = dv / dt

                    interpVal = slope * (inTime - keys[numKeys - 1].time) + keys[numKeys - 1].value
                }
            } else {
                // Otherwise if constant or in a cycle or oscillate, always use the last key value
                interpVal = keys[numKeys - 1].value
            }
        }

        return interpVal + cycleValueOffset
    }

    private fun evalForTwoKeys(key1: FRichCurveKey, key2: FRichCurveKey, inTime: Float): Float {
        val diff = key2.time - key1.time

        if (diff > 0f && key1.interpMode != ERichCurveInterpMode.RCIM_Constant) {
            val alpha = (inTime - key1.time) / diff
            val p0 = key1.value
            val p3 = key2.value

            if (key1.interpMode == ERichCurveInterpMode.RCIM_Linear) {
                return lerp(p0, p3, alpha)
            } else {
                if (isItNotWeighted(key1, key2)) {
                    val oneThird = 1.0f / 3.0f
                    val p1 = p0 + (key1.leaveTangent * diff * oneThird)
                    val p2 = p3 - (key2.arriveTangent * diff * oneThird)

                    return bezierInterp(p0, p1, p2, p3, alpha)
                } else { //it's weighted
                    return weightedEvalForTwoKeys(
                        key1.value, key1.time, key1.leaveTangent, key1.leaveTangentWeight, key1.tangentWeightMode,
                        key2.value, key2.time, key2.arriveTangent, key2.arriveTangentWeight, key2.tangentWeightMode,
                        inTime)
                }
            }
        } else {
            return key1.value
        }
    }
}

fun bezierInterp(p0: Float, p1: Float, p2: Float, p3: Float, alpha: Float): Float {
    val p01 = lerp(p0, p1, alpha)
    val p12 = lerp(p1, p2, alpha)
    val p23 = lerp(p2, p3, alpha)
    val p012 = lerp(p01, p12, alpha)
    val p123 = lerp(p12, p23, alpha)
    val p0123 = lerp(p012, p123, alpha)

    return p0123
}

fun bezierToPower(a1: Double, b1: Double, c1: Double, d1: Double,
                  out: DoubleArray) {
    val a = b1 - a1
    val b = c1 - b1
    val c = d1 - c1
    val d = b - a
    /*a2*/ out[3] = c - b - d
    /*b2*/ out[2] = 3.0 * d
    /*c2*/ out[1] = 3.0 * a
    /*d2*/ out[0] = a1
}

fun weightedEvalForTwoKeys(
    key1Value: Float, key1Time: Float, key1LeaveTangent: Float, key1LeaveTangentWeight: Float, key1TangentWeightMode: ERichCurveTangentWeightMode,
    key2Value: Float, key2Time: Float, key2ArriveTangent: Float, key2ArriveTangentWeight: Float, key2TangentWeightMode: ERichCurveTangentWeightMode,
    inTime: Float): Float {
    val diff = key2Time - key1Time
    val alpha = (inTime - key1Time) / diff
    val p0 = key1Value
    val p3 = key2Value
    val oneThird = 1.0f / 3.0f
    val time1 = key1Time
    val time2 = key2Time
    val x = time2 - time1
    var angle = atan(key1LeaveTangent)
    var cosAngle = cos(angle)
    var sinAngle = sin(angle)
    val leaveWeight = if (key1TangentWeightMode == RCTWM_WeightedNone || key1TangentWeightMode == RCTWM_WeightedArrive) {
        val leaveTangentNormalized = key1LeaveTangent
        val y = leaveTangentNormalized * x
        sqrt(x * x + y * y) * oneThird
    } else {
        key1LeaveTangentWeight
    }
    val key1TanX = cosAngle * leaveWeight + time1
    val key1TanY = sinAngle * leaveWeight + key1Value

    angle = atan(key2ArriveTangent)
    cosAngle = cos(angle)
    sinAngle = sin(angle)
    val arriveWeight = if (key2TangentWeightMode == RCTWM_WeightedNone || key2TangentWeightMode == RCTWM_WeightedLeave) {
        val arriveTangentNormalized = key2ArriveTangent
        val y = arriveTangentNormalized * x
        sqrt(x * x + y * y) * oneThird
    } else {
        key2ArriveTangentWeight
    }
    val key2TanX = -cosAngle * arriveWeight + time2
    val key2TanY = -sinAngle * arriveWeight + key2Value

    //Normalize the Time Range
    val rangeX = time2 - time1

    val dx1 = key1TanX - time1
    val dx2 = key2TanX - time1

    // Normalize values
    val normalizedX1 = dx1 / rangeX
    val normalizedX2 = dx2 / rangeX

    val coeff = DoubleArray(4)
    val results = DoubleArray(3)

    //Convert Bezier to Power basis, also float to double for precision for root finding.
    bezierToPower(
        0.0, normalizedX1.toDouble(), normalizedX2.toDouble(), 1.0,
        coeff
    )

    coeff[0] = coeff[0] - alpha

    val numResults = solveCubic(coeff, results)
    var newInterp = alpha
    if (numResults == 1) {
        newInterp = results[0].toFloat()
    } else {
        newInterp = Float.MIN_VALUE //just need to be out of range
        for (result in results) {
            if ((result >= 0.0f) && (result <= 1.0f)) {
                if (newInterp < 0.0f || result > newInterp) {
                    newInterp = result.toFloat()
                }
            }
        }

        if (newInterp == Float.MIN_VALUE) {
            newInterp = 0f
        }

    }
    //now use newInterp and adjusted tangents plugged into the Y (Value) part of the graph.
    //val p0 = key1.value
    val p1 = key1TanY
    //val p3 = key2.value
    val p2 = key2TanY

    val outValue = bezierInterp(p0, p1, p2, p3, newInterp)
    return outValue
}

fun isItNotWeighted(key1: FRichCurveKey, key2: FRichCurveKey) =
    ((key1.tangentWeightMode == RCTWM_WeightedNone || key1.tangentWeightMode == RCTWM_WeightedArrive)
        && (key2.tangentWeightMode == RCTWM_WeightedNone || key2.tangentWeightMode == RCTWM_WeightedLeave))