@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.converters.ue4.meshes.psk.psk

import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class VMeshUV(
    val u : Float,
    val v : Float
) {
    fun serialize(Ar : FArchiveWriter) {
        Ar.writeFloat32(u)
        Ar.writeFloat32(v)
    }
}