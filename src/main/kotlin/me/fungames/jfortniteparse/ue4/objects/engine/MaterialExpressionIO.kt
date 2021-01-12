package me.fungames.jfortniteparse.ue4.objects.engine

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.objects.core.math.FColor
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector2D
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

open class FExpressionInput : UClass {
    /** Index into Expression's outputs array that this input is connected to. */
    var outputIndex: Int
    var inputName: FName
    var mask: Int
    var maskR: Int
    var maskG: Int
    var maskB: Int
    var maskA: Int
    /** Material expression name that this input is connected to, or None if not connected. Used only in cooked builds */
    var expressionName: FName

    constructor(Ar: FArchive) {
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

    open fun serialize(Ar: FArchiveWriter) {
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

open class FMaterialInput<InputType> : FExpressionInput {
    var useConstant: Boolean
    var constant: InputType

    constructor(Ar: FArchive, init: () -> InputType) : super(Ar) {
        useConstant = Ar.readUInt32() != 0u
        constant = init()
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
        expressionName: FName,
        useConstant: Boolean,
        constant: InputType
    ) : super(outputIndex, inputName, mask, maskR, maskG, maskB, maskA, expressionName) {
        this.useConstant = useConstant
        this.constant = constant
    }

    fun serialize(Ar: FArchiveWriter, write: (InputType) -> Unit) {
        super.serialize(Ar)
        Ar.writeUInt32(if (useConstant) 1u else 0u)
        write(constant)
        super.completeWrite(Ar)
    }
}

class FColorMaterialInput : FMaterialInput<FColor> {
    constructor(Ar: FArchive) : super(Ar, { FColor(Ar) })

    constructor(
        outputIndex: Int,
        inputName: FName,
        mask: Int,
        maskR: Int,
        maskG: Int,
        maskB: Int,
        maskA: Int,
        expressionName: FName,
        useConstant: Boolean,
        constant: FColor
    ) : super(outputIndex, inputName, mask, maskR, maskG, maskB, maskA, expressionName, useConstant, constant)

    override fun serialize(Ar: FArchiveWriter) = super.serialize(Ar) { it.serialize(Ar) }
}

class FScalarMaterialInput : FMaterialInput<Float> {
    constructor(Ar: FArchive) : super(Ar, { Ar.readFloat32() })

    constructor(
        outputIndex: Int,
        inputName: FName,
        mask: Int,
        maskR: Int,
        maskG: Int,
        maskB: Int,
        maskA: Int,
        expressionName: FName,
        useConstant: Boolean,
        constant: Float
    ) : super(outputIndex, inputName, mask, maskR, maskG, maskB, maskA, expressionName, useConstant, constant)

    override fun serialize(Ar: FArchiveWriter) = super.serialize(Ar) { Ar.writeFloat32(it) }
}

class FVectorMaterialInput : FMaterialInput<FVector> {
    constructor(Ar: FArchive) : super(Ar, { FVector(Ar) })

    constructor(
        outputIndex: Int,
        inputName: FName,
        mask: Int,
        maskR: Int,
        maskG: Int,
        maskB: Int,
        maskA: Int,
        expressionName: FName,
        useConstant: Boolean,
        constant: FVector
    ) : super(outputIndex, inputName, mask, maskR, maskG, maskB, maskA, expressionName, useConstant, constant)

    override fun serialize(Ar: FArchiveWriter) = super.serialize(Ar) { it.serialize(Ar) }
}

class FVector2MaterialInput : FMaterialInput<FVector2D> {
    constructor(Ar: FArchive) : super(Ar, { FVector2D(Ar) })

    constructor(
        outputIndex: Int,
        inputName: FName,
        mask: Int,
        maskR: Int,
        maskG: Int,
        maskB: Int,
        maskA: Int,
        expressionName: FName,
        useConstant: Boolean,
        constant: FVector2D
    ) : super(outputIndex, inputName, mask, maskR, maskG, maskB, maskA, expressionName, useConstant, constant)

    override fun serialize(Ar: FArchiveWriter) = super.serialize(Ar) { it.serialize(Ar) }
}

class FMaterialAttributesInput : FExpressionInput {
    constructor(Ar: FArchive) : super(Ar)

    constructor(
        outputIndex: Int,
        inputName: FName,
        mask: Int,
        maskR: Int,
        maskG: Int,
        maskB: Int,
        maskA: Int,
        expressionName: FName
    ) : super(outputIndex, inputName, mask, maskR, maskG, maskB, maskA, expressionName)
}