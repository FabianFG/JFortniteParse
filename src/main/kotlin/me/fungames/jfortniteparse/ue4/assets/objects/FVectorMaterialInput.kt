package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class FVectorMaterialInput : UClass {
    var parent: FMaterialInput
    var useConstant: Boolean
    var constant : FVector
    var temp : Boolean
    var tempType : Short

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        parent = FMaterialInput(Ar)
        useConstant = Ar.readFlag()
        constant = FVector(Ar)
        temp = Ar.readFlag()
        tempType = Ar.readInt16()
        super.complete(Ar)
    }

    constructor(
        parent: FMaterialInput,
        useConstant: Boolean,
        constant: FVector,
        temp: Boolean,
        tempType: Short
    ) : super() {
        this.parent = parent
        this.useConstant = useConstant
        this.constant = constant
        this.temp = temp
        this.tempType = tempType
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        parent.serialize(Ar)
        Ar.writeFlag(useConstant)
        constant.serialize(Ar)
        Ar.writeFlag(temp)
        Ar.writeInt16(tempType)
        super.completeWrite(Ar)
    }
}