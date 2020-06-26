package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class TMovieSceneEvaluationTree<T> : UClass {
    var baseTree : FMovieSceneEvaluationTree
    var data : TEvaluationTreeEntryContainer<T>

    constructor(Ar : FArchive) {
        super.init(Ar)
        baseTree = FMovieSceneEvaluationTree(Ar)
        data = TEvaluationTreeEntryContainer(Ar)
        super.complete(Ar)
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        baseTree.serialize(Ar)
        data.serialize(Ar)
        super.completeWrite(Ar)
    }

    constructor(baseTree: FMovieSceneEvaluationTree, data: TEvaluationTreeEntryContainer<T>) {
        this.baseTree = baseTree
        this.data = data
    }
}