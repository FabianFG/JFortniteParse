package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class FObjectImport : UClass {
    var classPackage: FName
    var className: FName
    var outerIndex: FPackageIndex
    var objectName: FName

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