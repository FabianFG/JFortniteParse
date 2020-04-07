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
        range = TRange(Ar)
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
        range.serialize(Ar)
        parent.serialize(Ar)
        childrenId.serialize(Ar)
        dataId.serialize(Ar)
        super.completeWrite(Ar)
    }
}