package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FWeightedRandomSampler : UClass {
    var prob : Array<Float>
    var alias : Array<Int>
    var totalWeight : Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        prob = Ar.readTArray { Ar.readFloat32() }
        alias = Ar.readTArray { Ar.readInt32() }
        totalWeight = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeTArray(prob) {Ar.writeFloat32(it)}
        Ar.writeTArray(alias) {Ar.writeInt32(it)}
        Ar.writeFloat32(totalWeight)
        super.completeWrite(Ar)
    }

    constructor(prob : Array<Float>, alias : Array<Int>, totalWeight : Float) {
        this.prob = prob
        this.alias = alias
        this.totalWeight = totalWeight
    }
}