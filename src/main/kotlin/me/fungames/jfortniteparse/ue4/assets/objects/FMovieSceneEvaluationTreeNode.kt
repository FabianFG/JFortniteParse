package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FMovieSceneEvaluationTreeNode : UClass {
    var range : TRange<Int>
    var parent : FMovieSceneEvaluationTreeNodeHandle
    var childrenId : FEvaluationTreeEntryHandle
    var dataId : FEvaluationTreeEntryHandle

    constructor(Ar : FArchive) {
        super.init(Ar)
        range = TRange(Ar) { Ar.readInt32() }
        parent = FMovieSceneEvaluationTreeNodeHandle(Ar)
        childrenId = FEvaluationTreeEntryHandle(Ar)
        dataId = FEvaluationTreeEntryHandle(Ar)
        super.complete(Ar)
    }

    constructor(range: TRange<Int>, parent: FMovieSceneEvaluationTreeNodeHandle, childrenId: FEvaluationTreeEntryHandle, dataId: FEvaluationTreeEntryHandle) {
        this.range = range
        this.parent = parent
        this.childrenId = childrenId
        this.dataId = dataId
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        range.serialize(Ar) { Ar.writeInt32(it) }
        parent.serialize(Ar)
        childrenId.serialize(Ar)
        dataId.serialize(Ar)
        super.completeWrite(Ar)
    }
}