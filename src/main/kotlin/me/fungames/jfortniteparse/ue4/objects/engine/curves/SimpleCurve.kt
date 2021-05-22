package me.fungames.jfortniteparse.ue4.objects.engine.curves

import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.objects.core.math.isNearlyZero
import me.fungames.jfortniteparse.ue4.objects.core.math.lerp
import me.fungames.jfortniteparse.ue4.objects.engine.curves.ERichCurveExtrapolation.*
import me.fungames.jfortniteparse.ue4.objects.engine.curves.ERichCurveInterpMode.RCIM_Constant
import me.fungames.jfortniteparse.ue4.objects.engine.curves.ERichCurveInterpMode.RCIM_Linear
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import kotlin.jvm.internal.Ref.FloatRef
import kotlin.jvm.internal.Ref.IntRef
import kotlin.math.max
import kotlin.math.min

/** One key in a rich, editable float curve */
class FSimpleCurveKey {
    /** Time at this key */
    var time: Float = 0f
    /** Value at this key */
    var value: Float = 0f

    constructor(Ar: FArchive) {
        time = Ar.readFloat32()
        value = Ar.readFloat32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeFloat32(time)
        Ar.writeFloat32(value)
    }

    constructor(time: Float, value: Float) {
        this.time = time
        this.value = value
    }
}

/** A rich, editable float curve */
@UStruct
class FSimpleCurve : FRealCurve() {
    /** Interpolation mode between this key and the next */
    @JvmField
    @UProperty("InterpMode")
    var interpMode = RCIM_Linear
    /** Sorted array of keys */
    @JvmField
    @UProperty("Keys")
    var keys = mutableListOf<FSimpleCurveKey>()

    /** Get range of input time values. Outside this region curve continues constantly the start/end values. */
    override fun getTimeRange(minTime: FloatRef, maxTime: FloatRef) {
        if (keys.size == 0) {
            minTime.element = 0f
            maxTime.element = 0f
        } else {
            minTime.element = keys[0].time
            maxTime.element = keys[keys.size - 1].time
        }
    }

    /** Get range of output values. */
    override fun getValueRange(minValue: FloatRef, maxValue: FloatRef) {
        if (keys.size == 0) {
            minValue.element = 0f
            maxValue.element = 0f
        } else {
            minValue.element = keys[0].value
            maxValue.element = keys[0].value

            for (key in keys) {
                minValue.element = min(minValue.element, key.value)
                maxValue.element = max(maxValue.element, key.value)
            }
        }
    }

    /** Clear all keys. */
    override fun reset() {
        keys.clear()
        // KeyHandlesToIndices.clear()
    }

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

    /** Evaluate this curve at the specified time */
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

    private fun evalForTwoKeys(key1: FSimpleCurveKey, key2: FSimpleCurveKey, inTime: Float): Float {
        val diff = key2.time - key1.time

        if (diff > 0f && interpMode != RCIM_Constant) {
            val alpha = (inTime - key1.time) / diff
            val p0 = key1.value
            val p3 = key2.value

            return lerp(p0, p3, alpha)
        } else {
            return key1.value
        }
    }
}