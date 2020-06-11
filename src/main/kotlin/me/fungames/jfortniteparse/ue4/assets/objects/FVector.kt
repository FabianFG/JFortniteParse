package me.fungames.jfortniteparse.ue4.assets.objects

import glm_.vec3.Vec3
import glm_.vec4.Vec4
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
open class FVector : UClass {
    var x: Float
    var y: Float
    var z: Float

    constructor(packedRGBA16N: FPackedRGBA16N) {
        this.x = (packedRGBA16N.x.toFloat() - 32767.5f) / 32767.5f
        this.y = (packedRGBA16N.y.toFloat() - 32767.5f) / 32767.5f
        this.z = (packedRGBA16N.z.toFloat() - 32767.5f) / 32767.5f
    }

    constructor(Ar: FArchive) {
        super.init(Ar)
        x = Ar.readFloat32()
        y = Ar.readFloat32()
        z = Ar.readFloat32()
        super.complete(Ar)
    }

    open fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(x)
        Ar.writeFloat32(y)
        Ar.writeFloat32(z)
        super.completeWrite(Ar)
    }

    fun toVec3() = Vec3(x, y, z)
    fun toVec4() = Vec4(x, y, z, 0)

    operator fun minus(other : FVector) = FVector(x - other.x, y - other.y, z - other.z)
    operator fun plus(other : FVector) = FVector(x + other.x, y + other.y, z + other.z)

    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }
}