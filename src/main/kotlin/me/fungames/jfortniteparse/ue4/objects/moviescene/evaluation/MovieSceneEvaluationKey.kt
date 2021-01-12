package me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FMovieSceneEvaluationKey : UClass {
    var sequenceId: UInt
    var trackId: Int
    var sectionIndex: UInt

    constructor(Ar: FArchive) {
        super.init(Ar)
        sequenceId = Ar.readUInt32()
        trackId = Ar.readInt32()
        sectionIndex = Ar.readUInt32()
        super.complete(Ar)
    }

    constructor(sequenceId: UInt, trackId: Int, sectionIndex: UInt) {
        this.sequenceId = sequenceId
        this.trackId = trackId
        this.sectionIndex = sectionIndex
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(sequenceId)
        Ar.writeInt32(trackId)
        Ar.writeUInt32(sectionIndex)
        super.completeWrite(Ar)
    }
}