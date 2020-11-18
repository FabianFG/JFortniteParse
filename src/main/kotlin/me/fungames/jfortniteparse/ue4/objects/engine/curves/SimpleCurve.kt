package me.fungames.jfortniteparse.ue4.objects.engine.curves

import com.google.gson.annotations.SerializedName
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.objects.core.math.isNearlyZero
import me.fungames.jfortniteparse.ue4.objects.core.math.lerp
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import kotlin.jvm.internal.Ref
import kotlin.jvm.internal.Ref.FloatRef
import kotlin.math.max
import kotlin.math.min

/** One key in a rich, editable float curve */
class FSimpleCurveKey : UClass {
    /** Time at this key */
    var time: Float = 0f
    /** Value at this key */
    var value: Float = 0f

    constructor(Ar: FArchive) {
        super.init(Ar)
        time = Ar.readFloat32()
        value = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(time)
        Ar.writeFloat32(value)
        super.completeWrite(Ar)
    }

    constructor(time: Float, value: Float) {
        this.time = time
        this.value = value
    }
}

/** A rich, editable float curve */
@UStruct
class SimpleCurve : FRealCurve() {
    /** Interpolation mode between this key and the next */
    @SerializedName("InterpMode")
    var InterpMode: ERichCurveInterpMode = ERichCurveInterpMode.RCIM_Linear
    /** Sorted array of keys */
    @SerializedName("Keys")
    lateinit var Keys: MutableList<FSimpleCurveKey>

    /** Get range of input time values. Outside this region curve continues constantly the start/end values. */
    override fun getTimeRange(minTime: FloatRef, maxTime: FloatRef) {
        if (Keys.size == 0) {
            minTime.element = 0f
            maxTime.element = 0f
        } else {
            minTime.element = Keys[0].time
            maxTime.element = Keys[Keys.size - 1].time
        }
    }

    /** Get range of output values. */
    override fun getValueRange(minValue: FloatRef, maxValue: FloatRef) {
        if (Keys.size == 0) {
            minValue.element = 0f
            maxValue.element = 0f
        } else {
            minValue.element = Keys[0].value
            maxValue.element = Keys[0].value

            for (key in Keys) {
                minValue.element = min(minValue.element, key.value)
                maxValue.element = max(maxValue.element, key.value)
            }
        }
    }

    /** Clear all keys. */
    override fun reset() {
        Keys.clear()
        // KeyHandlesToIndices.clear()
    }

    /** Remap inTime based on pre and post infinity extrapolation values */
    fun remapTimeValue(inTime: FloatRef, cycleValueOffset: FloatRef) {
        val numKeys = Keys.size

        if (numKeys < 2) {
            return
        }

        if (inTime.element <= Keys[0].time) {
            if (preInfinityExtrap != ERichCurveExtrapolation.RCCE_Linear && preInfinityExtrap != ERichCurveExtrapolation.RCCE_Constant) {
                val minTime = Keys[0].time
                val maxTime = Keys[numKeys - 1].time

                val cycleCount = Ref.IntRef().apply { element = 0 }
                cycleTime(minTime, maxTime, inTime, cycleCount)

                if (preInfinityExtrap == ERichCurveExtrapolation.RCCE_CycleWithOffset) {
                    val dv = Keys[0].value - Keys[numKeys - 1].value
                    cycleValueOffset.element = dv * cycleCount.element
                } else if (preInfinityExtrap == ERichCurveExtrapolation.RCCE_Oscillate) {
                    if (cycleCount.element % 2 == 1) {
                        inTime.element = minTime + (maxTime - inTime.element)
                    }
                }
            }
        } else if (inTime.element >= Keys[numKeys - 1].time) {
            if (postInfinityExtrap != ERichCurveExtrapolation.RCCE_Linear && postInfinityExtrap != ERichCurveExtrapolation.RCCE_Constant) {
                val minTime = Keys[0].time
                val maxTime = Keys[numKeys - 1].time

                val cycleCount = Ref.IntRef().apply { element = 0 }
                cycleTime(minTime, maxTime, inTime, cycleCount)

                if (postInfinityExtrap == ERichCurveExtrapolation.RCCE_CycleWithOffset) {
                    val dv = Keys[numKeys - 1].value - Keys[0].value
                    cycleValueOffset.element = dv * cycleCount.element
                } else if (postInfinityExtrap == ERichCurveExtrapolation.RCCE_Oscillate) {
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

        val numKeys = Keys.size

        // If the default value hasn't been initialized, use the incoming default value
        var interpVal = if (defaultValue == Float.MAX_VALUE) inDefaultValue else defaultValue

        if (numKeys == 0) {
            // If no keys in curve, return the Default value.
        } else if (numKeys < 2 || (inTime <= Keys[0].time)) {
            if (preInfinityExtrap == ERichCurveExtrapolation.RCCE_Linear && numKeys > 1) {
                val dt = Keys[1].time - Keys[0].time

                if (isNearlyZero(dt)) {
                    interpVal = Keys[0].value
                } else {
                    val dv = Keys[1].value - Keys[0].value
                    val slope = dv / dt

                    interpVal = slope * (inTime - Keys[0].time) + Keys[0].value
                }
            } else {
                // Otherwise if constant or in a cycle or oscillate, always use the first key value
                interpVal = Keys[0].value
            }
        } else if (inTime < Keys[numKeys - 1].time) {
            // perform a lower bound to get the second of the interpolation nodes
            var first = 1
            val last = numKeys - 1
            var count = last - first

            while (count > 0) {
                val step = count / 2
                val middle = first + step

                if (inTime >= Keys[middle].time) {
                    first = middle + 1
                    count -= step + 1
                } else {
                    count = step
                }
            }

            interpVal = evalForTwoKeys(Keys[first - 1], Keys[first], inTime)
        } else {
            if (postInfinityExtrap == ERichCurveExtrapolation.RCCE_Linear) {
                val dt = Keys[numKeys - 2].time - Keys[numKeys - 1].time

                if (isNearlyZero(dt)) {
                    interpVal = Keys[numKeys - 1].value
                } else {
                    val dv = Keys[numKeys - 2].value - Keys[numKeys - 1].value
                    val slope = dv / dt

                    interpVal = slope * (inTime - Keys[numKeys - 1].time) + Keys[numKeys - 1].value
                }
            } else {
                // Otherwise if constant or in a cycle or oscillate, always use the last key value
                interpVal = Keys[numKeys - 1].value
            }
        }

        return interpVal + cycleValueOffset
    }

    private fun evalForTwoKeys(key1: FSimpleCurveKey, key2: FSimpleCurveKey, inTime: Float): Float {
        val diff = key2.time - key1.time

        if (diff > 0f && InterpMode != ERichCurveInterpMode.RCIM_Constant) {
            val alpha = (inTime - key1.time) / diff
            val p0 = key1.value
            val p3 = key2.value

            return lerp(p0, p3, alpha)
        } else {
            return key1.value
        }
    }
}