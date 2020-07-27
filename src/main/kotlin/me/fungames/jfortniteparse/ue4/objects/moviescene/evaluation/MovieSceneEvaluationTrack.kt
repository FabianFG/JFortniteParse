package me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.objects.FStructFallback
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FSectionEvaluationDataTree : UClass {
    var tree: TMovieSceneEvaluationTree<FStructFallback>

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        tree = TMovieSceneEvaluationTree(Ar) { Ar.readTArray { FStructFallback(Ar) } }
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        tree.serialize(Ar)
        super.completeWrite(Ar)
    }

    constructor(tree: TMovieSceneEvaluationTree<FStructFallback>) {
        this.tree = tree
    }
}