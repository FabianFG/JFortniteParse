package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FMovieSceneEvaluationTreeNodeHandle : UClass {
    var childrenHandle : FEvaluationTreeEntryHandle
    var index : Int

    constructor(Ar : FArchive) {
        super.init(Ar)
        childrenHandle = FEvaluationTreeEntryHandle(Ar)
        index = Ar.readInt32()
        super.complete(Ar)
    }

    constructor(childrenHandle: FEvaluationTreeEntryHandle, index: Int) {
        this.childrenHandle = childrenHandle
        this.index = index
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        childrenHandle.serialize(Ar)
        Ar.writeInt32(index)
        super.completeWrite(Ar)
    }
}