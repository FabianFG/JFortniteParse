package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.io.FIoContainerId
import me.fungames.jfortniteparse.ue4.objects.uobject.FMinimalName
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.util.get

typealias FSourceToLocalizedPackageIdMap = Array<Pair<FPackageId, FPackageId>>
typealias FCulturePackageMap = Map<String, FSourceToLocalizedPackageIdMap>

class FMappedName {
    companion object {
        private val INVALID_INDEX = 0u.inv()
        private val INDEX_BITS = 30u
        private val INDEX_MASK = (1u shl INDEX_BITS.toInt()) - 1u
        private val TYPE_MASK = INDEX_MASK.inv()
        private val TYPE_SHIFT = INDEX_BITS

        @JvmStatic
        fun create(index: UInt, number: UInt, type: EType): FMappedName {
            check(index <= Int.MAX_VALUE.toUInt())
            return FMappedName((type.ordinal.toUInt() shl TYPE_SHIFT.toInt()) or index, number)
        }

        @JvmStatic
        fun fromMinimalName(minimalName: FMinimalName) =
            FMappedName(minimalName.index.value, minimalName.number.toUInt())

        @JvmStatic
        inline fun isResolvedToMinimalName(minimalName: FMinimalName): Boolean {
            // Not completely safe, relies on that no FName will have its Index and Number equal to Max_uint32
            val mappedName = fromMinimalName(minimalName)
            return mappedName.isValid()
        }

        /*@JvmStatic
        inline fun safeMinimalNameToName(minimalName: FMinimalName): FName {
            return if (isResolvedToMinimalName(minimalName)) minimalNameToName(minimalName) else NAME_None;
        }*/
    }

    enum class EType {
        Package,
        Container,
        Global
    }

    private val index: UInt
    val number: UInt

    private constructor(index: UInt = INVALID_INDEX, number: UInt = INVALID_INDEX) {
        this.index = index
        this.number = number
    }

    constructor(Ar: FArchive) : this(Ar.readUInt32(), Ar.readUInt32())

    //fun toUnresolvedMinimalName() = FMinimalName(FNameEntryId(index), number.toInt())

    fun isValid() = index != INVALID_INDEX && number != INVALID_INDEX

    fun getType() = EType.values()[(index and TYPE_MASK) shr TYPE_SHIFT.toInt()]

    fun isGlobal() = ((index and TYPE_MASK) shr TYPE_SHIFT.toInt()) != 0u

    fun getIndex() = index and INDEX_MASK

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FMappedName

        if (index != other.index) return false
        if (number != other.number) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index.hashCode()
        result = 31 * result + number.hashCode()
        return result
    }
}

class FContainerHeader {
    var containerId: FIoContainerId
    var packageCount = 0u
    var names: ByteArray
    var nameHashes: ByteArray
    var packageIds: Array<FPackageId>
    var storeEntries: ByteArray
    var culturePackageMap: FCulturePackageMap
    var packageRedirects: Array<Pair<FPackageId, FPackageId>>

    constructor(Ar: FArchive) {
        containerId = FIoContainerId(Ar)
        packageCount = Ar.readUInt32()
        names = Ar.read(Ar.read())
        nameHashes = Ar.read(Ar.read())
        packageIds = Ar.readTArray { FPackageId(Ar) }
        storeEntries = Ar.read(Ar.read())
        culturePackageMap = Ar.readTMap { Ar.readString() to Ar.readTArray { FPackageId(Ar) to FPackageId(Ar) } }
        packageRedirects = Ar.readTArray { FPackageId(Ar) to FPackageId(Ar) }
    }
}

class FPackageObjectIndex {
    companion object {
        val INDEX_BITS = 62uL
        val INDEX_MASK = (1uL shl INDEX_BITS.toInt()) - 1uL
        val TYPE_MASK = INDEX_MASK.inv()
        val TYPE_SHIFT = INDEX_BITS
        val INVALID = 0uL.inv()

        fun generateImportHashFromObjectPath(objectPath: String): ULong {
            return 0uL
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
        typeAndId = (type.ordinal.toULong() shl TYPE_SHIFT.toInt()) or id
    }

    constructor(Ar: FArchive) {
        typeAndId = Ar.readUInt64()
    }

    fun isNull() = typeAndId == INVALID

    fun isExport() = (typeAndId shr TYPE_SHIFT.toInt()) == EType.Export.ordinal.toULong()

    fun isImport() = isScriptImport() || isPackageImport()

    fun isScriptImport() = (typeAndId shr TYPE_SHIFT.toInt()) == EType.ScriptImport.ordinal.toULong()

    fun isPackageImport() = (typeAndId shr TYPE_SHIFT.toInt()) == EType.PackageImport.ordinal.toULong()

    fun toExport(): UInt {
        check(isExport())
        return typeAndId.toUInt()
    }

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
 * Export bundle entry.
 */
class FExportBundleEntry {
    enum class EExportCommandType {
        ExportCommandType_Create,
        ExportCommandType_Serialize,
        ExportCommandType_Count
    }

    var localExportIndex: UInt
    var commandType: UInt

    constructor(Ar: FArchive) {
        localExportIndex = Ar.readUInt32()
        commandType = Ar.readUInt32()
    }
}

class FPackageStoreEntry {
    var exportBundlesSize = 0uL
    var exportCount = 0
    var exportBundleCount = 0
    var loadOrder = 0u
    var pad = 0u
    var importedPackages: Array<FPackageId>

    constructor(Ar: FArchive) {
        exportBundlesSize = Ar.readUInt64()
        exportCount = Ar.readInt32()
        exportBundleCount = Ar.readInt32()
        loadOrder = Ar.readUInt32()
        pad = Ar.readUInt32()
        val importedPackagesInitialPos = Ar.pos()
        val importedPackagesArrayNum = Ar.readUInt32()
        val importedPackagesOffsetToDataFromThis = Ar.readUInt32()
        Ar.seek(importedPackagesInitialPos + importedPackagesOffsetToDataFromThis.toInt())
        importedPackages = Ar.readTArray(importedPackagesArrayNum.toInt()) { FPackageId(Ar) }
        Ar.seek(importedPackagesInitialPos + 4 /*arrayNum*/ + 4 /* offsetToDataFromThis*/)
    }
}

/**
 * Export bundle header
 */
class FExportBundleHeader {
    var firstEntryIndex: UInt
    var entryCount: UInt

    constructor(Ar: FArchive) {
        this.firstEntryIndex = Ar.readUInt32()
        this.entryCount = Ar.readUInt32()
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
    var cookedSerialOffset = 0uL
    var cookedSerialSize = 0uL
    var objectName: FMappedName
    var outerIndex: FPackageObjectIndex
    var classIndex: FPackageObjectIndex
    var superIndex: FPackageObjectIndex
    var templateIndex: FPackageObjectIndex
    var globalImportIndex: FPackageObjectIndex
    var objectFlags: UInt
    var filterFlags: UByte
    //uint8 Pad[3] = {};

    constructor(Ar: FArchive) {
        cookedSerialOffset = Ar.readUInt64()
        cookedSerialSize = Ar.readUInt64()
        objectName = FMappedName(Ar)
        outerIndex = FPackageObjectIndex(Ar)
        classIndex = FPackageObjectIndex(Ar)
        superIndex = FPackageObjectIndex(Ar)
        templateIndex = FPackageObjectIndex(Ar)
        globalImportIndex = FPackageObjectIndex(Ar)
        objectFlags = Ar.readUInt32()
        filterFlags = Ar.readUInt8()
        Ar.skip(3)
    }
}

var GIsInitialLoad = true
val GUObjectArray = FUObjectArray()

fun GFindExistingScriptImport(
    globalImportIndex: FPackageObjectIndex,
    scriptObjects: MutableMap<FPackageObjectIndex, UObject?>,
    scriptObjectEntriesMap: Map<FPackageObjectIndex, FScriptObjectEntry>): UObject? =
    scriptObjects.getOrPut(globalImportIndex) {
        val entry = scriptObjectEntriesMap[globalImportIndex]
        check(entry != null)
        var obj: UObject?
        if (entry.outerIndex.isNull()) {
            obj = staticFindObjectFast(Package::class.java, null, entry.objectName.toName(), true)
        } else {
            val outer = GFindExistingScriptImport(entry.outerIndex, scriptObjects, scriptObjectEntriesMap)
            obj = scriptObjects[globalImportIndex] ?: throw AssertionError()
            if (outer != null) {
                obj = staticFindObjectFast(UObject::class.java, outer, entry.objectName.toName(), false, true)
            }
        }
        obj
    }

fun staticFindObjectFast(clazz: Class<*>, outer: UObject?, name: FName, exactClass: Boolean, anyPackage: Boolean = false): UObject? {
    TODO("Not yet implemented")
}