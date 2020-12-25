package me.fungames.jfortniteparse.ue4.objects

import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.assets.exports.FCurveTableRowHandle
import me.fungames.jfortniteparse.ue4.objects.engine.curves.FRealCurve

@UStruct
class FScalableFloat {
	/** Raw value, is multiplied by curve */
	@UProperty("Value")
	var value: Float = 0f

	/** Curve that is evaluated at a specific level. If found, it is multipled by Value */
	@UProperty("Curve")
	var curve: FCurveTableRowHandle? = null

	/** Cached direct pointer to the RealCurve we should evaluate */
	private var finalCurve: FRealCurve? = null

	/** Returns the scaled value at a given level */
	fun getValueAtLevel(level: Float): Float {
		if (curve?.curveTable != null) {
			if (finalCurve == null) {
				finalCurve = curve!!.getCurve()
			}

			if (finalCurve != null) {
				return value * finalCurve!!.eval(level)
			}
		}

		return value
	}

	/** Returns the scaled value at level 0 */
	fun getValue0() = getValueAtLevel(0f)
}