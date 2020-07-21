package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.versions.*

@ExperimentalUnsignedTypes
class FObjectExport : UClass {
    var classIndex: FPackageIndex
    var superIndex: FPackageIndex
    var templateIndex: FPackageIndex
    var outerIndex: FPackageIndex
    var objectName: FName
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