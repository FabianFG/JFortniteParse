@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.ue4.converters.meshes.psk

import me.fungames.jfortniteparse.ue4.converters.meshes.psk.common.VChunkHeader
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

internal fun FArchiveWriter.saveChunkHeader(header: VChunkHeader, name: String) {
    header.chunkId = name
    header.typeFlag = 20100422
    header.serialize(this)
}