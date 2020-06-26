package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class FObjectExport : UClass {
    var classIndex: FPackageIndex
    var superIndex: FPackageIndex
    var templateIndex: FPackageIndex
    var outerIndex: FPackageIndex
    var objectName: FName
    var save: UInt
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
    var serializationBeforeSerializationDependencies: Boolean
    var createBeforeSerializationDependencies: Boolean
    var serializationBeforeCreateDependencies: Boolean
    var createBeforeCreateDependencies: Boolean

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        classIndex = FPackageIndex(Ar)
        superIndex = FPackageIndex(Ar)
        templateIndex = FPackageIndex(Ar)
        outerIndex = FPackageIndex(Ar)
        objectName = Ar.readFName()
        save = Ar.readUInt32()
        serialSize = Ar.readInt64()
        serialOffset = Ar.readInt64()
        forcedExport = Ar.readBoolean()
        notForClient = Ar.readBoolean()
        notForServer = Ar.readBoolean()
        packageGuid = FGuid(Ar)
        packageFlags = Ar.readUInt32()
        notAlwaysLoadedForEditorGame = Ar.readBoolean()
        isAsset = Ar.readBoolean()
        firstExportDependency = Ar.readInt32()
        serializationBeforeSerializationDependencies = Ar.readBoolean()
        createBeforeSerializationDependencies = Ar.readBoolean()
        serializationBeforeCreateDependencies = Ar.readBoolean()
        createBeforeCreateDependencies = Ar.readBoolean()
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        classIndex.serialize(Ar)
        superIndex.serialize(Ar)
        templateIndex.serialize(Ar)
        outerIndex.serialize(Ar)
        Ar.writeFName(objectName)
        Ar.writeUInt32(save)
        Ar.writeInt64(serialSize)
        Ar.writeInt64(serialOffset)
        Ar.writeBoolean(forcedExport)
        Ar.writeBoolean(notForClient)
        Ar.writeBoolean(notForServer)
        packageGuid.serialize(Ar)
        Ar.writeUInt32(packageFlags)
        Ar.writeBoolean(notAlwaysLoadedForEditorGame)
        Ar.writeBoolean(isAsset)
        Ar.writeInt32(firstExportDependency)
        Ar.writeBoolean(serializationBeforeSerializationDependencies)
        Ar.writeBoolean(createBeforeSerializationDependencies)
        Ar.writeBoolean(serializationBeforeCreateDependencies)
        Ar.writeBoolean(createBeforeCreateDependencies)
        super.completeWrite(Ar)
    }

    constructor(
        classIndex: FPackageIndex,
        superIndex: FPackageIndex,
        templateIndex: FPackageIndex,
        outerIndex: FPackageIndex,
        objectName: FName,
        save: UInt,
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
        serializationBeforeSerializationDependencies: Boolean,
        createBeforeSerializationDependencies: Boolean,
        serializationBeforeCreateDependencies: Boolean,
        createBeforeCreateDependencies: Boolean
    ) {
        this.classIndex = classIndex
        this.superIndex = superIndex
        this.templateIndex = templateIndex
        this.outerIndex = outerIndex
        this.objectName = objectName
        this.save = save
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
}