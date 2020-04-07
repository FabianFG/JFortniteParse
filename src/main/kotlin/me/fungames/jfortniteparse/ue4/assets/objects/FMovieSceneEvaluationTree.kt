package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FMovieSceneEvaluationTree : UClass {
    var rootNode : FMovieSceneEvaluationTreeNode
    var childNodes : TEvaluationTreeEntryContainer<FMovieSceneEvaluationTreeNode>

    constructor(Ar : FArchive) {
        super.init(Ar)
        rootNode = FMovieSceneEvaluationTreeNode(Ar)
        childNodes = TEvaluationTreeEntryContainer(Ar)
        super.complete(Ar)
    }

    constructor(rootNode: FMovieSceneEvaluationTreeNode, childNodes: TEvaluationTreeEntryContainer<FMovieSceneEvaluationTreeNode>) : super() {
        this.rootNode = rootNode
        this.childNodes = childNodes
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        rootNode.serialize(Ar)
        childNodes.serialize(Ar)
        super.completeWrite(Ar)
    }
}