@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.ue4.converters.meshes.psk.pskx

import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

// The same as VTriangle16 but with 32-bit vertex indices.
// NOTE: this structure has different on-disk and in-memory layout and size (due to alignment).
class VTriangle32(
    val wedgeIndex : IntArray,       // Point to three vertices in the vertex list.
    val matIndex : Byte,                // Materials can be anything.
    val auxMatIndex : Byte,             // Second material (unused).
    val smoothingGroups : UInt          // 32-bit flag for smoothing groups.
) {
    fun serialize(Ar : FArchiveWriter) {
        Ar.writeInt32(wedgeIndex[0])
        Ar.writeInt32(wedgeIndex[1])
        Ar.writeInt32(wedgeIndex[2])
        Ar.writeInt8(matIndex)
        Ar.writeInt8(auxMatIndex)
        Ar.writeUInt32(smoothingGroups)
    }
}