package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.ue4.objects.core.serialization.FCustomVersion
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.FMappedName
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.FPackageFileVersion
import me.fungames.jfortniteparse.ue4.versions.GAME_UE5_BASE
import me.fungames.jfortniteparse.util.CityHash.cityHash64

class FPackageImportReference(val importedPackageIndex: UInt, val importedPublicExportHashIndex: UInt)

class FPackageObjectIndex {
    companion object {
        val INDEX_BITS = 62
        val INDEX_MASK = (1uL shl INDEX_BITS) - 1uL
        val TYPE_MASK = INDEX_MASK.inv()
        val TYPE_SHIFT = INDEX_BITS
        val INVALID = 0uL.inv()

        fun generateImportHashFromObjectPath(objectPath: String): ULong {
            val fullImportPath = StringBuilder(objectPath)
            fullImportPath.forEachIndexed { i, c ->
                if (c == '.' || c == ':') {
                    fullImportPath[i] = '/'
                } else {
                    fullImportPath[i] = c.toLowerCase()
                }
            }
            val data = fullImportPath.toString().toByteArray(Charsets.UTF_16LE)
            var hash = cityHash64(data, 0, data.size).toULong()
            hash = hash and (3uL shl 62).inv()
            return hash
        }

        fun fromExportIndex(index: Int) =
            FPackageObjectIndex(EType.Export, index.toULong())

        fun fromScriptPath(scriptObjectPath: String) =
            FPackageObjectIndex(EType.ScriptImport, generateImportHashFromObjectPath(scriptObjectPath))

        fun fromPackagePath(packageObjectPath: String) =
            FPackageObjectIndex(EType.PackageImport, generateImportHashFromObjectPath(packageObjectPath))
    }

    private var typeAndId = INVALID

    enum class EType {
        Export,
        ScriptImport,
        PackageImport,
        Null
    }

    constructor()

    constructor(type: EType, id: ULong) {
        typeAndId = (type.ordinal.toULong() shl TYPE_SHIFT) or id
    }

    constructor(Ar: FArchive) {
        typeAndId = Ar.readUInt64()
    }

    fun isNull() = typeAndId == INVALID

    fun isExport() = (typeAndId shr TYPE_SHIFT).toInt() == EType.Export.ordinal

    fun isImport() = isScriptImport() || isPackageImport()

    fun isScriptImport() = (typeAndId shr TYPE_SHIFT).toInt() == EType.ScriptImport.ordinal

    fun isPackageImport() = (typeAndId shr TYPE_SHIFT).toInt() == EType.PackageImport.ordinal

    fun toExport(): UInt {
        check(isExport())
        return typeAndId.toUInt()
    }

    fun toPackageImportRef(): FPackageImportReference {
        val importedPackageIndex = ((typeAndId and INDEX_MASK) shr 32).toUInt()
        val exportHash = typeAndId.toUInt()
        return FPackageImportReference(importedPackageIndex, exportHash)
    }

    fun type() = EType.values()[(typeAndId shr TYPE_SHIFT).toInt()] // custom

    fun value() = typeAndId and INDEX_MASK

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FPackageObjectIndex

        if (typeAndId != other.typeAndId) return false

        return true
    }

    override fun hashCode() = typeAndId.hashCode()
}

/**
 * Package summary.
 */
class FPackageSummary {
    var name: FMappedName
    var sourceName: FMappedName
    var packageFlags: UInt
    var cookedHeaderSize: UInt
    var nameMapNamesOffset: Int
    var nameMapNamesSize: Int
    var nameMapHashesOffset: Int
    var nameMapHashesSize: Int
    var importMapOffset: Int
    var exportMapOffset: Int
    var exportBundlesOffset: Int
    var graphDataOffset: Int
    var graphDataSize: Int
    var pad: Int /*= 0*/

    constructor(Ar: FArchive) {
        name = FMappedName(Ar)
        sourceName = FMappedName(Ar)
        packageFlags = Ar.readUInt32()
        cookedHeaderSize = Ar.readUInt32()
        nameMapNamesOffset = Ar.readInt32()
        nameMapNamesSize = Ar.readInt32()
        nameMapHashesOffset = Ar.readInt32()
        nameMapHashesSize = Ar.readInt32()
        importMapOffset = Ar.readInt32()
        exportMapOffset = Ar.readInt32()
        exportBundlesOffset = Ar.readInt32()
        graphDataOffset = Ar.readInt32()
        graphDataSize = Ar.readInt32()
        pad = Ar.readInt32()
    }
}

object EZenPackageVersion {
    const val Initial = 0

    const val Latest = Initial
}

class FZenPackageVersioningInfo {
    var version: Int
    var packageVersion: FPackageFileVersion
    var licenseeVersion: Int
    var customVersions: Array<FCustomVersion>

    constructor(Ar: FArchive) {
        version = Ar.readInt32()
        packageVersion = FPackageFileVersion(Ar)
        licenseeVersion = Ar.readInt32()
        customVersions = Ar.readTArray { FCustomVersion(Ar) }
    }
}

/**
 * Package summary.
 */
class FZenPackageSummary {
    var bHasVersioningInfo: Boolean
    var headerSize: UInt
    var name: FMappedName
    var packageFlags: UInt
    var cookedHeaderSize: UInt
    var importedPublicExportHashesOffset: Int
    var importMapOffset: Int
    var exportMapOffset: Int
    var exportBundleEntriesOffset: Int
    var graphDataOffset: Int

    constructor(Ar: FArchive) {
        bHasVersioningInfo = Ar.readBoolean()
        headerSize = Ar.readUInt32()
        name = FMappedName(Ar)
        packageFlags = Ar.readUInt32()
        cookedHeaderSize = Ar.readUInt32()
        importedPublicExportHashesOffset = Ar.readInt32()
        importMapOffset = Ar.readInt32()
        exportMapOffset = Ar.readInt32()
        exportBundleEntriesOffset = Ar.readInt32()
        graphDataOffset = Ar.readInt32()
    }
}

/**
 * Export bundle entry.
 */
class FExportBundleEntry {
    enum class EExportCommandType {
        ExportCommandType_Create,
        ExportCommandType_Serialize,
        ExportCommandType_Count
    }

    var localExportIndex: Int
    var commandType: EExportCommandType

    constructor(Ar: FArchive) {
        localExportIndex = Ar.readInt32()
        commandType = EExportCommandType.values()[Ar.readInt32()]
    }
}

/**
 * Export bundle header
 */
class FExportBundleHeader {
    var serialOffset: ULong
    var firstEntryIndex: UInt
    var entryCount: UInt

    constructor(Ar: FArchive) {
        serialOffset = if (Ar.game >= GAME_UE5_BASE) Ar.readUInt64() else ULong.MAX_VALUE
        firstEntryIndex = Ar.readUInt32()
        entryCount = Ar.readUInt32()
    }
}

class FExportMapEntry {
    companion object {
        const val SIZE = 72
    }

    var cookedSerialOffset = 0uL
    var cookedSerialSize = 0uL
    var objectName: FMappedName
    var outerIndex: FPackageObjectIndex
    var classIndex: FPackageObjectIndex
    var superIndex: FPackageObjectIndex
    var templateIndex: FPackageObjectIndex
    var globalImportIndex: FPackageObjectIndex
    var publicExportHash: Long
    var objectFlags: UInt
    var filterFlags: UByte

    constructor(Ar: FArchive) {
        val start = Ar.pos()
        cookedSerialOffset = Ar.readUInt64()
        cookedSerialSize = Ar.readUInt64()
        objectName = FMappedName(Ar)
        outerIndex = FPackageObjectIndex(Ar)
        classIndex = FPackageObjectIndex(Ar)
        superIndex = FPackageObjectIndex(Ar)
        templateIndex = FPackageObjectIndex(Ar)
        if (Ar.game >= GAME_UE5_BASE) {
            globalImportIndex = FPackageObjectIndex()
            publicExportHash = Ar.readInt64()
        } else {
            globalImportIndex = FPackageObjectIndex(Ar)
            publicExportHash = 0
        }
        objectFlags = Ar.readUInt32()
        filterFlags = Ar.readUInt8()
        Ar.seek(start + SIZE)
    }
}

class FBulkDataMapEntry {
    companion object {
        const val SIZE = 32
    }

    var serialOffset: Long
    var duplicateSerialOffset: Long
    var serialSize: Long
    var flags: Int

    constructor(Ar: FArchive) {
        val start = Ar.pos()
        serialOffset = Ar.readInt64()
        duplicateSerialOffset = Ar.readInt64()
        serialSize = Ar.readInt64()
        flags = Ar.readInt32()
        Ar.seek(start + SIZE)
    }
}