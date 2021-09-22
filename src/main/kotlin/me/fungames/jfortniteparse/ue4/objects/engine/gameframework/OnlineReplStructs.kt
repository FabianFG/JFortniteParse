package me.fungames.jfortniteparse.ue4.objects.engine.gameframework

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FUniqueNetId
import me.fungames.jfortniteparse.ue4.objects.uobject.FUniqueNetIdWrapper
import me.fungames.jfortniteparse.ue4.reader.FArchive

const val INVALID_UNIQUE_NET_ID_STR = "INVALID"

@JsonAdapter(FUniqueNetIdRepl.Serializer::class)
class FUniqueNetIdRepl : FUniqueNetIdWrapper {
    var replicationBytes: ByteArray? = null
    override var uniqueNetId: FUniqueNetId<*>?
        get() = super.uniqueNetId
        set(value) {
            replicationBytes = null
            super.uniqueNetId = value
        }

    constructor()

    constructor(Ar: FArchive) {
        val size = Ar.readInt32()
        if (size > 0) {
            val type = Ar.readFName()
            val contents = Ar.readString()
            uniqueIdFromString(type, contents)
        } else {
            uniqueNetId = null
        }
    }

    /** Helper to create an FUniqueNetId from a string and its type */
    private fun uniqueIdFromString(type: FName, contents: String) {
        uniqueNetId = FUniqueNetId(type.text, contents)
    }

    class Serializer : TypeAdapter<FUniqueNetIdRepl>() {
        override fun write(out: JsonWriter, value: FUniqueNetIdRepl) {
            val uniqueNetId = value.uniqueNetId
            if (uniqueNetId != null) {
                out.value(uniqueNetId.type + ':' + uniqueNetId)
            } else {
                out.value(INVALID_UNIQUE_NET_ID_STR)
            }
        }

        override fun read(`in`: JsonReader): FUniqueNetIdRepl {
            TODO("Not yet implemented")
        }
    }
}