package me.fungames.jfortniteparse.ue4.objects.core.math

/**
 * 4x4 matrix of floating point values.
 * Matrix-matrix multiplication happens with a pre-multiple of the transpose --
 * in other words, `res = mat1 * mat2` means `res = mat2^T * mat1`, as
 * opposed to `res = mat1 * mat2`.
 * Matrix elements are accessed with `m[rowIndex][columnIndex]`.
 */
@ExperimentalUnsignedTypes
open class FMatrix {
    val m: Array<FloatArray> = Array(4) { FloatArray(4) }

    /** Homogeneous transform. */
    fun transformFVector4(p: FVector4) = FVector4(
        p.x * m[0][0] + p.y * m[1][0] + p.z * m[2][0] + p.w * m[3][0],
        p.x * m[0][1] + p.y * m[1][1] + p.z * m[2][1] + p.w * m[3][1],
        p.x * m[0][2] + p.y * m[1][2] + p.z * m[2][2] + p.w * m[3][2],
        p.x * m[0][3] + p.y * m[1][3] + p.z * m[2][3] + p.w * m[3][3]
    )

    /**
     * Transform a direction vector - will not take into account translation part of the FMatrix.
     * If you want to transform a surface normal (or plane) and correctly account for non-uniform scaling you should use transformByUsingAdjointT.
     */
    fun transformVector(v: FVector) = transformFVector4(FVector4(v.x, v.y, v.z, 0f))

    /** Transpose. */
    fun getTransposed(): FMatrix {
        val result = FMatrix()

        result.m[0][0] = m[0][0]
        result.m[0][1] = m[1][0]
        result.m[0][2] = m[2][0]
        result.m[0][3] = m[3][0]

        result.m[1][0] = m[0][1]
        result.m[1][1] = m[1][1]
        result.m[1][2] = m[2][1]
        result.m[1][3] = m[3][1]

        result.m[2][0] = m[0][2]
        result.m[2][1] = m[1][2]
        result.m[2][2] = m[2][2]
        result.m[2][3] = m[3][2]

        result.m[3][0] = m[0][3]
        result.m[3][1] = m[1][3]
        result.m[3][2] = m[2][3]
        result.m[3][3] = m[3][3]

        return result
    }

    /** Apply Scale to this matrix **/
//    fun applyScale(scale: Float)

    /** @return the origin of the co-ordinate system */
    fun getOrigin() = FVector(m[3][0], m[3][1], m[3][2])

    /**
     * Get a textual representation of the vector.
     *
     * @return Text describing the vector.
     */
    override fun toString(): String {
        var output = ""

        output += "[%g %g %g %g] ".format(m[0][0], m[0][1], m[0][2], m[0][3])
        output += "[%g %g %g %g] ".format(m[1][0], m[1][1], m[1][2], m[1][3])
        output += "[%g %g %g %g] ".format(m[2][0], m[2][1], m[2][2], m[2][3])
        output += "[%g %g %g %g] ".format(m[3][0], m[3][1], m[3][2], m[3][3])

        return output
    }
}