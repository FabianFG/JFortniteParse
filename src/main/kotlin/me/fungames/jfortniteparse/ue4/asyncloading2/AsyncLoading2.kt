package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.ue4.io.FIoContainerId
import me.fungames.jfortniteparse.ue4.objects.uobject.FMinimalName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageId
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.FMappedName
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.FNameMap
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.GAME_UE5_BASE
import me.fungames.jfortniteparse.util.CityHash.cityHash64

class FContainerHeaderPackageRedirect {
    var sourcePackageId: FPackageId
    var targetPackageId: FPackageId
    var sourcePackageName: FMappedName?

    constructor(Ar: FArchive) {
        sourcePackageId = FPackageId(Ar)
        targetPackageId = FPackageId(Ar)
        sourcePackageName = if (Ar.game >= GAME_UE5_BASE) FMappedName(Ar) else null
    }
}

typealias FSourceToLocalizedPackageIdMap = Array<FContainerHeaderPackageRedirect>
typealias FCulturePackageMap = Map<String, FSourceToLocalizedPackageIdMap>

class FContainerHeader {
    var containerId: FIoContainerId
    var packageCount = 0u
    val containerNameMap = FNameMap()
    var packageIds: Array<FPackageId>
    var storeEntries: Array<FPackageStoreEntry>
    var culturePackageMap: FCulturePackageMap
    var packageRedirects: Array<FContainerHeaderPackageRedirect>

    constructor(Ar: FArchive) {
        containerId = FIoContainerId(Ar)
        packageCount = Ar.readUInt32()
        if (Ar.game < GAME_UE5_BASE) {
            val names = Ar.read(Ar.readInt32())
            val nameHashes = Ar.read(Ar.readInt32())
            if (names.isNotEmpty()) {
                containerNameMap.load(names, nameHashes, FMappedName.EType.Container)
            }
        }
        packageIds = Ar.readTArray { FPackageId(Ar) }
        val storeEntriesNum = Ar.readInt32()
        val storeEntriesEnd = Ar.pos() + storeEntriesNum
        storeEntries = Array(packageCount.toInt()) { FPackageStoreEntry(Ar) }
        Ar.seek(storeEntriesEnd)
        if (Ar.game >= GAME_UE5_BASE) {
            containerNameMap.load(Ar, FMappedName.EType.Container)
        }
        culturePackageMap = Ar.readTMap { Ar.readString() to Ar.readTArray { FContainerHeaderPackageRedirect(Ar) } }
        packageRedirects = Ar.readTArray { FContainerHeaderPackageRedirect(Ar) }
    }
}

class FPackageImportReference(val importedPackageIndex: UInt, val exportHash: UInt)

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

/**
 * Package summary.
 */
class FPackageSummary5 {
    var headerSize: UInt
    var name: FMappedName
    //var sourceName: FMappedName
    var packageFlags: UInt
    var cookedHeaderSize: UInt
    var importMapOffset: Int
    var exportMapOffset: Int
    var exportBundleEntriesOffset: Int
    var graphDataOffset: Int
    var pad: Int /*= 0*/

    constructor(Ar: FArchive) {
        headerSize = Ar.readUInt32()
        name = FMappedName(Ar)
        //sourceName = FMappedName(Ar)
        packageFlags = Ar.readUInt32()
        cookedHeaderSize = Ar.readUInt32()
        importMapOffset = Ar.readInt32()
        exportMapOffset = Ar.readInt32()
        exportBundleEntriesOffset = Ar.readInt32()
        graphDataOffset = Ar.readInt32()
        pad = Ar.readInt32()
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

// Actual name: FFilePackageStoreEntry
class FPackageStoreEntry {
    var exportBundlesSize = 0uL
    var exportCount = 0
    var exportBundleCount = 0
    var loadOrder = 0u
    var pad = 0u
    var importedPackages: Array<FPackageId>
    //var shaderMapHashes: Array<ByteArray>

    constructor(Ar: FArchive) {
        exportBundlesSize = Ar.readUInt64()
        exportCount = Ar.readInt32()
        exportBundleCount = Ar.readInt32()
        loadOrder = Ar.readUInt32()
        pad = Ar.readUInt32()
        importedPackages = Ar.readCArrayView { FPackageId(Ar) }
        if (Ar.game >= GAME_UE5_BASE) {
            Ar.skip(8) //shaderMapHashes = Ar.readCArrayView { Ar.read(20) }
        }
    }

    private inline fun <reified T> FArchive.readCArrayView(init: (FArchive) -> T): Array<T> {
        val initialPos = pos()
        val arrayNum = readInt32()
        val offsetToDataFromThis = readInt32()
        if (arrayNum <= 0) {
            return emptyArray()
        }
        val continuePos = pos()
        seek(initialPos + offsetToDataFromThis)
        val result = Array(arrayNum) { init(this) }
        seek(continuePos)
        return result
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

class FScriptObjectEntry {
    var objectName: FMinimalName
    var globalIndex: FPackageObjectIndex
    var outerIndex: FPackageObjectIndex
    var cdoClassIndex: FPackageObjectIndex

    constructor(Ar: FArchive, nameMap: List<String>) {
        objectName = FMinimalName(Ar, nameMap)
        globalIndex = FPackageObjectIndex(Ar)
        outerIndex = FPackageObjectIndex(Ar)
        cdoClassIndex = FPackageObjectIndex(Ar)
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
    var exportHash: UInt
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
            exportHash = Ar.readUInt32()
        } else {
            globalImportIndex = FPackageObjectIndex(Ar)
            exportHash = 0u
        }
        objectFlags = Ar.readUInt32()
        filterFlags = Ar.readUInt8()
        Ar.seek(start + SIZE)
    }
}