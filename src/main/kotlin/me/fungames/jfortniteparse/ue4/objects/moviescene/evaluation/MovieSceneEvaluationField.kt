package me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation

import me.fungames.jfortniteparse.ue4.reader.FArchive

class FMovieSceneEvaluationFieldEntityTree {
    var serializedData: TMovieSceneEvaluationTree<FEntityAndMetaDataIndex>

    constructor(Ar: FArchive) {
        serializedData = TMovieSceneEvaluationTree(Ar) { Ar.readTArray { FEntityAndMetaDataIndex(Ar) } }
    }

    class FEntityAndMetaDataIndex(var entityIndex: Int, var metaDataIndex: Int) {
        constructor(Ar: FArchive) : this(Ar.readInt32(), Ar.readInt32())

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as FEntityAndMetaDataIndex

            if (entityIndex != other.entityIndex) return false
            if (metaDataIndex != other.metaDataIndex) return false

            return true
        }

        override fun hashCode(): Int {
            var result = entityIndex
            result = 31 * result + metaDataIndex
            return result
        }
    }
}