package me.fungames.jfortniteparse.ue4.objects.engine

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FWeightedRandomSampler {
    var prob: Array<Float>
    var alias: Array<Int>
    var totalWeight: Float

    constructor(Ar: FArchive) {
        prob = Ar.readTArray { Ar.readFloat32() }
        alias = Ar.readTArray { Ar.readInt32() }
        totalWeight = Ar.readFloat32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeTArray(prob) { Ar.writeFloat32(it) }
        Ar.writeTArray(alias) { Ar.writeInt32(it) }
        Ar.writeFloat32(totalWeight)
    }

    constructor(prob: Array<Float>, alias: Array<Int>, totalWeight: Float) {
        this.prob = prob
        this.alias = alias
        this.totalWeight = totalWeight
    }
}