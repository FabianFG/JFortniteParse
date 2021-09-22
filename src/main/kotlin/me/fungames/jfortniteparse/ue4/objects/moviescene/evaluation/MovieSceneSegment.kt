package me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation

import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.assets.objects.FStructFallback
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.core.math.TRange
import me.fungames.jfortniteparse.ue4.objects.core.misc.FFrameNumber
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

/** Enumeration specifying how to evaluate a particular section when inside a segment */
enum class ESectionEvaluationFlags(val value: UByte) {
    /** No special flags - normal evaluation */
    None        (0x00u),
    /** Segment resides inside the 'pre-roll' time for the section */
    PreRoll     (0x01u),
    /** Segment resides inside the 'post-roll' time for the section */
    PostRoll    (0x02u)
}

/**
 * Evaluation data that specifies information about what to evaluate for a given template
 */
@UStruct
class FSectionEvaluationData(
    /** The implementation index we should evaluate (index into FMovieSceneEvaluationTrack::ChildTemplates) */
    val ImplIndex: Int,

    /** A forced time to evaluate this section at */
    var ForcedTime: FFrameNumber,

    /** Additional flags for evaluating this section */
    var Flags: ESectionEvaluationFlags
)

/**
 * Information about a single segment of an evaluation track
 */
class FMovieSceneSegment {
    /** The segment's range */
    var range: TRange<FFrameNumber>
    var id: Int
    /** Whether this segment has been generated yet or not */
    var allowEmpty: Boolean
    /** Array of implementations that reside at the segment's range */
    var impls: Array<FStructFallback> // Array<FSectionEvaluationData>

    constructor(Ar: FAssetArchive) {
        range = TRange(Ar) { FFrameNumber(Ar) }
        id = Ar.readInt32()
        allowEmpty = Ar.readBoolean()
        impls = Ar.readTArray { FStructFallback(Ar, FName("SectionEvaluationData")) }
    }

    constructor(range: TRange<FFrameNumber>, id: Int, allowEmpty: Boolean, impls: Array<FStructFallback>) {
        this.range = range
        this.id = id
        this.allowEmpty = allowEmpty
        this.impls = impls
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        range.serialize(Ar) { it.serialize(Ar) }
        Ar.writeInt32(id)
        Ar.writeBoolean(allowEmpty)
        Ar.writeTArray(impls) { it.serialize(Ar) }
    }
}