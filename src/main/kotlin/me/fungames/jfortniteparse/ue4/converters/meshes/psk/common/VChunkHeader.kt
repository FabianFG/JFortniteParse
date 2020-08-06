@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.ue4.converters.meshes.psk.common

import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class VChunkHeader {
    var chunkId = ""
    var typeFlag = 0
    var dataSize = 0
    var dataCount = 0

    fun serialize(Ar : FArchiveWriter) {
        Ar.write(chunkId.toByteArray().copyOf(20))
        Ar.writeInt32(typeFlag)
        Ar.writeInt32(dataSize)
        Ar.writeInt32(dataCount)
    }
}