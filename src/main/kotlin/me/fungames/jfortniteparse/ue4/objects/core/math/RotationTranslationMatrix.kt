package me.fungames.jfortniteparse.ue4.objects.core.math

import kotlin.math.cos
import kotlin.math.sin

/** Combined rotation and translation matrix */
open class FRotationTranslationMatrix : FMatrix {
    /**
     * Constructor.
     *
     * @param rot rotation
     * @param origin translation to apply
     */
    constructor(rot: FRotator, origin: FVector) {
        val p = Math.toRadians(rot.pitch.toDouble())
        val y = Math.toRadians(rot.yaw.toDouble())
        val r = Math.toRadians(rot.roll.toDouble())
        val sP = sin(p).toFloat()
        val sY = sin(y).toFloat()
        val sR = sin(r).toFloat()
        val cP = cos(p).toFloat()
        val cY = cos(y).toFloat()
        val cR = cos(r).toFloat()

        m[0][0] = cP * cY
        m[0][1] = cP * sY
        m[0][2] = sP
        m[0][3] = 0f

        m[1][0] = sR * sP * cY - cR * sY
        m[1][1] = sR * sP * sY + cR * cY
        m[1][2] = -sR * cP
        m[1][3] = 0f

        m[2][0] = -(cR * sP * cY + sR * sY)
        m[2][1] = cY * sR - cR * sP * sY
        m[2][2] = cR * cP
        m[2][3] = 0f

        m[3][0] = origin.x
        m[3][1] = origin.y
        m[3][2] = origin.z
        m[3][3] = 1f
    }
}