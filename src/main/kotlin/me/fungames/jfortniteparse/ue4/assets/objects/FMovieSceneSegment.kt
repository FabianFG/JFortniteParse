package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class FMovieSceneSegment : UClass {
    /** The segment's range */
    var range : TRange<FFrameNumber>
    var id : Int
    /** Whether this segment has been generated yet or not */
    var allowEmpty : Boolean
    /** Array of implementations that reside at the segment's range */
    var impls : Array<UScriptStruct>

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        range = TRange(Ar) { FFrameNumber(Ar) }
        id = Ar.readInt32()
        allowEmpty = Ar.readBoolean()
        impls = Ar.readTArray { UScriptStruct(Ar, "SectionEvaluationData") }
        super.complete(Ar)
    }

    constructor(range: TRange<FFrameNumber>, id: Int, allowEmpty: Boolean, impls: Array<UScriptStruct>) : super() {
        this.range = range
        this.id = id
        this.allowEmpty = allowEmpty
        this.impls = impls
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        range.serialize(Ar) { it.serialize(Ar) }
        Ar.writeInt32(id)
        Ar.writeBoolean(allowEmpty)
        Ar.writeTArray(impls) { it.serialize(Ar) }
        super.completeWrite(Ar)
    }
}