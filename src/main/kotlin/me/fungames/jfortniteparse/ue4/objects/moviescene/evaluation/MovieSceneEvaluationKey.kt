package me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FMovieSceneEvaluationKey {
    var sequenceId: UInt
    var trackId: Int
    var sectionIndex: UInt

    constructor(Ar: FArchive) {
        sequenceId = Ar.readUInt32()
        trackId = Ar.readInt32()
        sectionIndex = Ar.readUInt32()
    }

    constructor(sequenceId: UInt, trackId: Int, sectionIndex: UInt) {
        this.sequenceId = sequenceId
        this.trackId = trackId
        this.sectionIndex = sectionIndex
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeUInt32(sequenceId)
        Ar.writeInt32(trackId)
        Ar.writeUInt32(sectionIndex)
    }
}