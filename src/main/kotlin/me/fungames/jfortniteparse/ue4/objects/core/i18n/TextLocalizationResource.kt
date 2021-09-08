package me.fungames.jfortniteparse.ue4.objects.core.i18n

import me.fungames.jfortniteparse.LOG_JFP
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FTextLocalizationResource {
    val stringData: MutableMap<String, MutableMap<String, String>>

    constructor(Ar: FArchive, fileName: String = "Unnamed LocRes") {
        val magicNumber = FGuid(Ar)
        var version = FTextLocalizationResourceVersion.ELocResVersion.Legacy
        if (magicNumber == FTextLocalizationResourceVersion.LOC_RES_MAGIC) {
            version = FTextLocalizationResourceVersion.ELocResVersion.values().getOrElse(Ar.read()) {
                throw ParserException("LocRes '$fileName' is too new to be loaded (File Version: $version, Loader Version: ${FTextLocalizationResourceVersion.ELocResVersion.values().size - 1})")
            }
        } else {
            // Legacy LocRes files lack the magic number, assume that's what we're dealing with, and seek back to the start of the file
            Ar.seek(0)
            LOG_JFP.warn("LocRes '$fileName' failed the magic number check! Assuming this is a legacy resource")
        }

        var localizedStringArray = emptyList<FTextLocalizationResourceString>()
        if (version >= FTextLocalizationResourceVersion.ELocResVersion.Compact) {
            val localizedStringArrayOffset = Ar.readInt64()
            if (localizedStringArrayOffset != -1L) {
                val currentFileOffset = Ar.pos()
                Ar.seek(localizedStringArrayOffset.toInt())
                localizedStringArray = if (version >= FTextLocalizationResourceVersion.ELocResVersion.Optimized_CRC32) {
                    Ar.readArray { FTextLocalizationResourceString(Ar) }
                } else {
                    val tmpLocalizedStringArray = Ar.readTArray { Ar.readString() }
                    tmpLocalizedStringArray.map { FTextLocalizationResourceString(it, -1) }
                }
                Ar.seek(currentFileOffset)
            }
        }

        // Read entries count
        if (version >= FTextLocalizationResourceVersion.ELocResVersion.Optimized_CRC32) {
            var entriesCount = Ar.readInt32()
        }

        // Read namespace count
        val namespaceCount = Ar.readInt32()
        stringData = LinkedHashMap(namespaceCount)
        repeat(namespaceCount) {
            val namespace = FTextKey(Ar, version)
            val keyCount = Ar.readInt32()
            val keyValue = LinkedHashMap<String, String>(keyCount)
            repeat(keyCount) {
                val key = FTextKey(Ar, version)
                var sourceHash = Ar.readUInt32()
                if (version >= FTextLocalizationResourceVersion.ELocResVersion.Compact) {
                    val localizedStringIndex = Ar.readInt32()
                    if (localizedStringIndex >= 0 && localizedStringIndex < localizedStringArray.size) {
                        keyValue[key.str] = localizedStringArray[localizedStringIndex].data
                    } else {
                        LOG_JFP.warn("LocRes '$fileName' has an invalid localized string index for namespace '${namespace.str}' and key '${key.str}'. This entry will have no translation.")
                    }
                } else {
                    keyValue[key.str] = Ar.readString()
                }
            }
            stringData[namespace.str] = keyValue
        }
    }
}

class FTextLocalizationResourceString {
    var data: String
    var refCount: Int

    constructor(Ar: FArchive) {
        data = Ar.readString()
        refCount = Ar.readInt32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeString(data)
        Ar.writeInt32(refCount)
    }

    constructor(data: String, refCount: Int) {
        this.data = data
        this.refCount = refCount
    }
}

class FTextKey {
    var str: String
    var strHash: UInt

    constructor(Ar: FArchive, version: FTextLocalizationResourceVersion.ELocResVersion) {
        strHash = if (version >= FTextLocalizationResourceVersion.ELocResVersion.Optimized_CRC32) Ar.readUInt32() else 0u
        str = Ar.readString()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeUInt32(strHash)
        Ar.writeString(str)
    }

    constructor(str: String, strHash: UInt) {
        this.str = str
        this.strHash = strHash
    }
}