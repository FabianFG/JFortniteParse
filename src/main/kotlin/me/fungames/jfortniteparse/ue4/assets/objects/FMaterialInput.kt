package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class FMaterialInput : UClass {
    /** Index into Expression's outputs array that this input is connected to. */
    var outputIndex : Int
    var inputName : FName
    var mask : Int
    var maskR : Int
    var maskG : Int
    var maskB : Int
    var maskA : Int
    /** Material expression name that this input is connected to, or None if not connected. Used only in cooked builds */
    var expressionName : FName

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        outputIndex = Ar.readInt32()
        inputName = Ar.readFName()
        mask = Ar.readInt32()
        maskR = Ar.readInt32()
        maskG = Ar.readInt32()
        maskB = Ar.readInt32()
        maskA = Ar.readInt32()
        expressionName = Ar.readFName()
        super.complete(Ar)
    }

    constructor(
        outputIndex: Int,
        inputName: FName,
        mask: Int,
        maskR: Int,
        maskG: Int,
        maskB: Int,
        maskA: Int,
        expressionName: FName
    ) {
        this.outputIndex = outputIndex
        this.inputName = inputName
        this.mask = mask
        this.maskR = maskR
        this.maskG = maskG
        this.maskB = maskB
        this.maskA = maskA
        this.expressionName = expressionName
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(outputIndex)
        Ar.writeFName(inputName)
        Ar.writeInt32(mask)
        Ar.writeInt32(maskR)
        Ar.writeInt32(maskG)
        Ar.writeInt32(maskB)
        Ar.writeInt32(maskA)
        Ar.writeFName(expressionName)
        super.completeWrite(Ar)
    }


}