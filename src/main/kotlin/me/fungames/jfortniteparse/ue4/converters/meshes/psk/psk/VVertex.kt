package me.fungames.jfortniteparse.ue4.converters.meshes.psk.psk

import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class VVertex(
    val pointIndex: Int,                       // int16, padded to int; used as int for large meshes
    val u: Float, val v: Float,
    val matIndex: Byte, val reserved: Byte,
    val pad: Short                             // not used
) {
    fun serialize(Ar: FArchiveWriter) {
        Ar.writeInt32(pointIndex)
        Ar.writeFloat32(u)
        Ar.writeFloat32(v)
        Ar.writeInt8(matIndex)
        Ar.writeInt8(reserved)
        Ar.writeInt16(pad)
    }
}