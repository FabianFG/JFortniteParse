@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.converters.ue4.meshes.psk.psk

import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class VMaterial(
    val materialName : String,
    val textureIndex : Int,
    val polyFlags : UInt,
    val auxMaterial : Int,
    val auxFlags : UInt,
    val lodBias : Int,
    val lodStyle : Int
) {
    fun serialize(Ar : FArchiveWriter) {
        Ar.write(materialName.toByteArray().copyOf(64))
        Ar.writeInt32(textureIndex)
        Ar.writeUInt32(polyFlags)
        Ar.writeInt32(auxMaterial)
        Ar.writeUInt32(auxFlags)
        Ar.writeInt32(lodBias)
        Ar.writeInt32(lodStyle)
    }
}