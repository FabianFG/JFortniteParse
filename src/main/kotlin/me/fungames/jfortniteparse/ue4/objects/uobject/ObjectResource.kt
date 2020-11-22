package me.fungames.jfortniteparse.ue4.objects.uobject

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.PakPackage
import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.versions.*
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/**
 * Wrapper for index into a ULinker's ImportMap or ExportMap.
 * Values greater than zero indicate that this is an index into the ExportMap.  The
 * actual array index will be (FPackageIndex - 1).
 *
 * Values less than zero indicate that this is an index into the ImportMap. The actual
 * array index will be (-FPackageIndex - 1)
 */
class FPackageIndex : UClass {
    /**
     * Values greater than zero indicate that this is an index into the ExportMap.  The
     * actual array index will be (FPackageIndex - 1).
     *
     * Values less than zero indicate that this is an index into the ImportMap. The actual
     * array index will be (-FPackageIndex - 1)
     */
    var index: Int
    var owner: Package? = null
    /*val importObject: FObjectImport?
        get() = if (isImport()) owner?.importMap?.getOrNull(toImport()) else null
    val outerImportObject: FObjectImport?
        get() = this.importObject?.outerIndex?.importObject ?: this.importObject

    val exportObject: FObjectExport?
        get() = if (isExport()) owner?.exportMap?.getOrNull(toExport()) else null

    val name: String
        get() = importObject?.objectName?.text
            ?: exportObject?.objectName?.text
            ?: "null"

    val resource: FObjectResource?
        get() = importObject ?: exportObject*/
    val name: String
        get() = (owner as PakPackage).run { getResource() }?.objectName?.text ?: "null"

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        index = Ar.readInt32()
        super.complete(Ar)
        owner = Ar.owner
    }

    /** Constructor, sets the value to null **/
    constructor() : this(0)

    constructor(index: Int, owner: Package? = null) {
        this.index = index
        this.owner = owner
    }

    /** return true if this is an index into the import map **/
    inline fun isImport() = index < 0

    /** return true if this is an index into the export map **/
    inline fun isExport() = index > 0

    /** return true if this null (i.e. neither an import nor an export) **/
    inline fun isNull() = index == 0

    /** Check that this is an import and return the index into the import map **/
    inline fun toImport(): Int {
        check(isImport())
        return -index - 1
    }

    /** Check that this is an export and return the index into the export map **/
    inline fun toExport(): Int {
        check(isExport())
        return index - 1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FPackageIndex

        if (index != other.index) return false
        if (owner != other.owner) return false

        return true
    }

    operator fun compareTo(other: FPackageIndex) = index.compareTo(other.index)

    /**
     * Serializes a package index value into an archive.
     */
    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(index)
        super.completeWrite(Ar)
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + (owner?.hashCode() ?: 0) // actually index only
        return result
    }

    /*override fun toString() = importObject?.objectName?.text?.let { "Import: $it" }
        ?: exportObject?.objectName?.text?.let { "Export: $it" }
        ?: index.toString()*/

    override fun toString() = when {
        isImport() -> "Import: ${toImport()}"
        isExport() -> "Export: ${toExport()}"
        else -> "null"
    }

    inline fun <reified T> load() = owner?.loadObject<T>(this)

    fun load() = owner?.loadObjectGeneric(this)
}

/**
 * Base class for UObject resource types.  FObjectResources are used to store UObjects on disk
 * via FLinker's ImportMap (for resources contained in other packages) and ExportMap (for resources
 * contained within the same package)
 */
abstract class FObjectResource : UClass() {
    lateinit var objectName: FName
    lateinit var outerIndex: FPackageIndex
}

/**
 * UObject resource type for objects that are contained within this package and can
 * be referenced by other packages.
 */
class FObjectExport : FObjectResource {
    var classIndex: FPackageIndex
    var superIndex: FPackageIndex
    var templateIndex: FPackageIndex
    var objectFlags: UInt
    var serialSize: Long
    var serialOffset: Long
    var forcedExport: Boolean
    var notForClient: Boolean
    var notForServer: Boolean
    var packageGuid: FGuid
    var packageFlags: UInt
    var notAlwaysLoadedForEditorGame: Boolean
    var isAsset: Boolean
    var firstExportDependency: Int
    var serializationBeforeSerializationDependencies: Int
    var createBeforeSerializationDependencies: Int
    var serializationBeforeCreateDependencies: Int
    var createBeforeCreateDependencies: Int
    @Transient lateinit var exportObject: Lazy<UExport>

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        classIndex = FPackageIndex(Ar)
        superIndex = FPackageIndex(Ar)
        templateIndex = if (Ar.ver >= VER_UE4_TemplateIndex_IN_COOKED_EXPORTS) FPackageIndex(Ar) else FPackageIndex()
        outerIndex = FPackageIndex(Ar)
        objectName = Ar.readFName()
        objectFlags = Ar.readUInt32()

        if (Ar.ver < VER_UE4_64BIT_EXPORTMAP_SERIALSIZES) {
            serialSize = Ar.readInt32().toLong()
            serialOffset = Ar.readInt32().toLong()
        } else {
            serialSize = Ar.readInt64()
            serialOffset = Ar.readInt64()
        }

        forcedExport = Ar.readBoolean()
        notForClient = Ar.readBoolean()
        notForServer = Ar.readBoolean()
        packageGuid = FGuid(Ar)
        packageFlags = Ar.readUInt32()
        notAlwaysLoadedForEditorGame = if (Ar.ver >= VER_UE4_LOAD_FOR_EDITOR_GAME) Ar.readBoolean() else true
        isAsset = if (Ar.ver >= VER_UE4_COOKED_ASSETS_IN_EDITOR_SUPPORT) Ar.readBoolean() else false

        if (Ar.ver >= VER_UE4_PRELOAD_DEPENDENCIES_IN_COOKED_EXPORTS) {
            firstExportDependency = Ar.readInt32()
            serializationBeforeSerializationDependencies = Ar.readInt32()
            createBeforeSerializationDependencies = Ar.readInt32()
            serializationBeforeCreateDependencies = Ar.readInt32()
            createBeforeCreateDependencies = Ar.readInt32()
        } else {
            firstExportDependency = -1
            serializationBeforeSerializationDependencies = 0
            createBeforeSerializationDependencies = 0
            serializationBeforeCreateDependencies = 0
            createBeforeCreateDependencies = 0
        }

        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        classIndex.serialize(Ar)
        superIndex.serialize(Ar)
        if (Ar.ver >= VER_UE4_TemplateIndex_IN_COOKED_EXPORTS) templateIndex.serialize(Ar)
        outerIndex.serialize(Ar)
        Ar.writeFName(objectName)
        Ar.writeUInt32(objectFlags)

        if (Ar.ver < VER_UE4_64BIT_EXPORTMAP_SERIALSIZES) {
            Ar.writeInt32(serialSize.toInt())
            Ar.writeInt32(serialOffset.toInt())
        } else {
            Ar.writeInt64(serialSize)
            Ar.writeInt64(serialOffset)
        }

        Ar.writeBoolean(forcedExport)
        Ar.writeBoolean(notForClient)
        Ar.writeBoolean(notForServer)
        packageGuid.serialize(Ar)
        Ar.writeUInt32(packageFlags)
        if (Ar.ver >= VER_UE4_LOAD_FOR_EDITOR_GAME) Ar.writeBoolean(notAlwaysLoadedForEditorGame)
        if (Ar.ver >= VER_UE4_COOKED_ASSETS_IN_EDITOR_SUPPORT) Ar.writeBoolean(isAsset)

        if (Ar.ver >= VER_UE4_PRELOAD_DEPENDENCIES_IN_COOKED_EXPORTS) {
            Ar.writeInt32(firstExportDependency)
            Ar.writeInt32(serializationBeforeSerializationDependencies)
            Ar.writeInt32(createBeforeSerializationDependencies)
            Ar.writeInt32(serializationBeforeCreateDependencies)
            Ar.writeInt32(createBeforeCreateDependencies)
        }

        super.completeWrite(Ar)
    }

    constructor(
        classIndex: FPackageIndex,
        superIndex: FPackageIndex,
        templateIndex: FPackageIndex,
        outerIndex: FPackageIndex,
        objectName: FName,
        objectFlags: UInt,
        serialSize: Long,
        serialOffset: Long,
        forcedExport: Boolean,
        notForClient: Boolean,
        notForServer: Boolean,
        packageGuid: FGuid,
        packageFlags: UInt,
        notAlwaysLoadedForEditorGame: Boolean,
        isAsset: Boolean,
        firstExportDependency: Int,
        serializationBeforeSerializationDependencies: Int,
        createBeforeSerializationDependencies: Int,
        serializationBeforeCreateDependencies: Int,
        createBeforeCreateDependencies: Int
    ) {
        this.classIndex = classIndex
        this.superIndex = superIndex
        this.templateIndex = templateIndex
        this.outerIndex = outerIndex
        this.objectName = objectName
        this.objectFlags = objectFlags
        this.serialSize = serialSize
        this.serialOffset = serialOffset
        this.forcedExport = forcedExport
        this.notForClient = notForClient
        this.notForServer = notForServer
        this.packageGuid = packageGuid
        this.packageFlags = packageFlags
        this.notAlwaysLoadedForEditorGame = notAlwaysLoadedForEditorGame
        this.isAsset = isAsset
        this.firstExportDependency = firstExportDependency
        this.serializationBeforeSerializationDependencies = serializationBeforeSerializationDependencies
        this.createBeforeSerializationDependencies = createBeforeSerializationDependencies
        this.serializationBeforeCreateDependencies = serializationBeforeCreateDependencies
        this.createBeforeCreateDependencies = createBeforeCreateDependencies
    }

    override fun toString() = objectName.text
}

/**
 * UObject resource type for objects that are referenced by this package, but contained
 * within another package.
 */
class FObjectImport : FObjectResource {
    var classPackage: FName
    var className: FName

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        classPackage = Ar.readFName()
        className = Ar.readFName()
        outerIndex = FPackageIndex(Ar)
        objectName = Ar.readFName()
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFName(classPackage)
        Ar.writeFName(className)
        outerIndex.serialize(Ar)
        Ar.writeFName(objectName)
        super.completeWrite(Ar)
    }

    constructor(classPackage: FName, className: FName, outerIndex: FPackageIndex, objectName: FName) {
        this.classPackage = classPackage
        this.className = className
        this.outerIndex = outerIndex
        this.objectName = objectName
    }

    override fun toString() = objectName.text
}