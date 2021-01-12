package me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.objects.FStructFallback
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

// Note: this struct was removed in this commit:
// https://github.com/EpicGames/UnrealEngine/commit/76506dfcdc705f50a9bde3b4bd08bd6514d2cd4d#diff-6fef167d7afd2b5d1e22e25965e6c8d0717bb388e426ee26c8be670283984547
class FSectionEvaluationDataTree : UClass {
    var tree: TMovieSceneEvaluationTree<FStructFallback> /*TMovieSceneEvaluationTree<FSectionEvaluationData>*/

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        tree = TMovieSceneEvaluationTree(Ar) { Ar.readTArray { FStructFallback(Ar, FName.dummy("SectionEvaluationData")) } }
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