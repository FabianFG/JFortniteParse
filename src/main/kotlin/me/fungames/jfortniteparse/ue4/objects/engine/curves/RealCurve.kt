package me.fungames.jfortniteparse.ue4.objects.engine.curves

import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.UStruct
import kotlin.jvm.internal.Ref.FloatRef
import kotlin.jvm.internal.Ref.IntRef
import kotlin.math.abs

/** Method of interpolation between this key and the next. */
enum class ERichCurveInterpMode {
    /** Use linear interpolation between values. */
    RCIM_Linear,
    /** Use a constant value. Represents stepped values. */
    RCIM_Constant,
    /** Cubic interpolation. See TangentMode for different cubic interpolation options. */
    RCIM_Cubic,
    /** No interpolation. */
    RCIM_None
}

/** Enumerates extrapolation options. */
enum class ERichCurveExtrapolation {
    /** Repeat the curve without an offset. */
    RCCE_Cycle,
    /** Repeat the curve with an offset relative to the first or last key's value. */
    RCCE_CycleWithOffset,
    /** Sinusoidally extrapolate. */
    RCCE_Oscillate,
    /** Use a linearly increasing value for extrapolation.*/
    RCCE_Linear,
    /** Use a constant value for extrapolation */
    RCCE_Constant,
    /** No Extrapolation */
    RCCE_None
}

/** A rich, editable float curve */
@UStruct
open class FRealCurve {
    /** Default value */
    @JvmField
    @UProperty("DefaultValue")
    val defaultValue = Float.MAX_VALUE
    /** Pre-infinity extrapolation state */
    @JvmField
    @UProperty("PreInfinityExtrap")
    val preInfinityExtrap = ERichCurveExtrapolation.RCCE_Constant
    /** Pre-infinity extrapolation state */
    @JvmField
    @UProperty("PostInfinityExtrap")
    val postInfinityExtrap = ERichCurveExtrapolation.RCCE_Constant

    /** Get range of input time values. Outside this region curve continues constantly the start/end values. */
    open fun getTimeRange(minTime: FloatRef, maxTime: FloatRef) {}

    /** Get range of output values. */
    open fun getValueRange(minValue: FloatRef, maxValue: FloatRef) {}

    /** Clear all keys. */
    open fun reset() {}

    /** Remap inTime based on pre and post infinity extrapolation values */
    open fun remapTimeValue(inTime: FloatRef, cycleValueOffset: FloatRef) {}

    /** Evaluate this curve at the specified time */
    @JvmOverloads
    open fun eval(inTime: Float, inDefaultValue: Float = 0.0f) = 0f

    companion object {
        @JvmStatic
        protected fun cycleTime(minTime: Float, maxTime: Float, inTime: FloatRef, cycleCount: IntRef) {
            val initTime = inTime.element
            val duration = maxTime - minTime

            if (inTime.element > maxTime) {
                cycleCount.element = ((maxTime - inTime.element) / duration).toInt()
                inTime.element = inTime.element + duration * cycleCount.element
            } else if (inTime.element < minTime) {
                cycleCount.element = ((inTime.element - minTime) / duration).toInt()
                inTime.element = inTime.element - duration * cycleCount.element
            }

            if (inTime.element == maxTime && initTime < minTime) {
                inTime.element = minTime
            }

            if (inTime.element == minTime && initTime > maxTime) {
                inTime.element = maxTime
            }

            cycleCount.element = abs(cycleCount.element)
        }
    }
}