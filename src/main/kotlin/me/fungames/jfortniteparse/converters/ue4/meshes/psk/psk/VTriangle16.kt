@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.converters.ue4.meshes.psk.psk

import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

// Textured triangle.
class VTriangle16(
    val wedgeIndex : UShortArray,       // Point to three vertices in the vertex list.
    val matIndex : Byte,                // Materials can be anything.
    val auxMatIndex : Byte,             // Second material (unused).
    val smoothingGroups : UInt          // 32-bit flag for smoothing groups.
) {
    fun serialize(Ar : FArchiveWriter) {
        Ar.writeUInt16(wedgeIndex[0])
        Ar.writeUInt16(wedgeIndex[1])
        Ar.writeUInt16(wedgeIndex[2])
        Ar.writeInt8(matIndex)
        Ar.writeInt8(auxMatIndex)
        Ar.writeUInt32(smoothingGroups)
    }
}