@file:JvmName("FMath")

package me.fungames.jfortniteparse.ue4.objects.core.math

import kotlin.math.abs

const val SMALL_NUMBER = 1e-8f
const val KINDA_SMALL_NUMBER = 1e-4f

/**
 * Checks if a floating point number is nearly zero.
 * @param value Number to compare
 * @param errorTolerance Maximum allowed difference for considering Value as 'nearly zero'
 * @return true if Value is nearly zero
 */
inline fun isNearlyZero(value: Float, errorTolerance: Float = SMALL_NUMBER) = abs(value) <= errorTolerance

/** Multiples value by itself */
inline fun square(a: Float) = a * a

/** Performs a linear interpolation between two values, alpha ranges from 0-1 */
inline fun lerp(a: Float, b: Float, alpha: Float) = a + alpha * (b - a)