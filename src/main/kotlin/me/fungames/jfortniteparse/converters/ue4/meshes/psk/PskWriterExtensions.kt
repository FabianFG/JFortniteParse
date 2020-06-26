@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.converters.ue4.meshes.psk

import glm_.vec3.Vec3
import me.fungames.jfortniteparse.converters.ue4.meshes.psk.common.VChunkHeader
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter


internal fun FArchiveWriter.saveChunkHeader(header : VChunkHeader, name : String) {
    header.chunkId = name
    header.typeFlag = 20100422
    header.serialize(this)
}

internal fun Vec3.serialize(Ar : FArchiveWriter) {
    Ar.writeFloat32(x)
    Ar.writeFloat32(y)
    Ar.writeFloat32(z)
}