package me.fungames.jfortniteparse.ue4.manifests.objects

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FCustomFields {
    var fields: Map<String, String>

    constructor(Ar: FArchive) {
        val startPos = Ar.pos()
        val dataSize = Ar.readUInt32()
        /*val dataVersionInt = */Ar.readUInt8()
        val elementCount = Ar.readInt32()

        data class MutablePair<A, B>(var first: A, var second: B)

        val arrayFields = Array(elementCount) { MutablePair("", "") }
        for (field in arrayFields) field.first = Ar.readString()
        for (field in arrayFields) field.second = Ar.readString()
        val fields = mutableMapOf<String, String>()
        for ((first, second) in arrayFields)
            fields[first] = second
        this.fields = fields
        Ar.seek(startPos + dataSize.toInt())
    }

    fun serialize(Ar: FArchiveWriter) {}
}