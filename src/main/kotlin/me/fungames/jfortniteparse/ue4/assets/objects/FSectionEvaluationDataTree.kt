package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter


@ExperimentalUnsignedTypes
class FSectionEvaluationDataTree : UClass {
    var tree : TMovieSceneEvaluationTree<UScriptStruct>

    constructor(Ar : FArchive) {
        super.init(Ar)
        tree = TMovieSceneEvaluationTree(Ar)
        super.complete(Ar)
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        tree.serialize(Ar)
        super.completeWrite(Ar)
    }

    constructor(tree: TMovieSceneEvaluationTree<UScriptStruct>) {
        this.tree = tree
    }
}