package me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation

import me.fungames.jfortniteparse.ue4.objects.core.math.TRange
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FEvaluationTreeEntryHandle {
    var entryIndex: Int

    constructor(Ar: FArchive) {
        entryIndex = Ar.readInt32()
    }

    constructor(entryIndex: Int) {
        this.entryIndex = entryIndex
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeInt32(entryIndex)
    }
}

class TEvaluationTreeEntryContainer<ElementType> {
    var entries: Array<FEntry>
    var items: Array<ElementType>

    constructor(Ar: FArchive, init: () -> Array<ElementType>) {
        entries = Ar.readTArray { FEntry(Ar) }
        items = init()
    }

    constructor(entries: Array<FEntry>, items: Array<ElementType>) {
        this.entries = entries
        this.items = items
    }

    fun serialize(Ar: FArchiveWriter, write: (Array<ElementType>) -> Unit) {
        Ar.writeTArray(entries) { it.serialize(Ar) }
        write(items)
    }

    class FEntry {
        /** The index into Items of the first item */
        var startIndex: Int
        /** The number of currently valid items */
        var size: Int
        /** The total capacity of allowed items before reallocating */
        var capacity: Int

        constructor(Ar: FArchive) {
                startIndex = Ar.readInt32()
            size = Ar.readInt32()
            capacity = Ar.readInt32()
        }

        constructor(startIndex: Int, size: Int, capacity: Int) {
            this.startIndex = startIndex
            this.size = size
            this.capacity = capacity
        }

        fun serialize(Ar: FArchiveWriter) {
            Ar.writeInt32(startIndex)
            Ar.writeInt32(size)
            Ar.writeInt32(capacity)
        }
    }
}

class FMovieSceneEvaluationTreeNodeHandle {
    var childrenHandle: FEvaluationTreeEntryHandle
    var index: Int

    constructor(Ar: FArchive) {
        childrenHandle = FEvaluationTreeEntryHandle(Ar)
        index = Ar.readInt32()
    }

    constructor(childrenHandle: FEvaluationTreeEntryHandle, index: Int) {
        this.childrenHandle = childrenHandle
        this.index = index
    }

    fun serialize(Ar: FArchiveWriter) {
        childrenHandle.serialize(Ar)
        Ar.writeInt32(index)
    }
}

class FMovieSceneEvaluationTreeNode {
    var range: TRange<Int>
    var parent: FMovieSceneEvaluationTreeNodeHandle
    var childrenId: FEvaluationTreeEntryHandle
    var dataId: FEvaluationTreeEntryHandle

    constructor(Ar: FArchive) {
        range = TRange(Ar) { Ar.readInt32() }
        parent = FMovieSceneEvaluationTreeNodeHandle(Ar)
        childrenId = FEvaluationTreeEntryHandle(Ar)
        dataId = FEvaluationTreeEntryHandle(Ar)
    }

    constructor(range: TRange<Int>, parent: FMovieSceneEvaluationTreeNodeHandle, childrenId: FEvaluationTreeEntryHandle, dataId: FEvaluationTreeEntryHandle) {
        this.range = range
        this.parent = parent
        this.childrenId = childrenId
        this.dataId = dataId
    }

    fun serialize(Ar: FArchiveWriter) {
        range.serialize(Ar) { Ar.writeInt32(it) }
        parent.serialize(Ar)
        childrenId.serialize(Ar)
        dataId.serialize(Ar)
    }
}

open class FMovieSceneEvaluationTree {
    var rootNode: FMovieSceneEvaluationTreeNode
    var childNodes: TEvaluationTreeEntryContainer<FMovieSceneEvaluationTreeNode>

    constructor(Ar: FArchive) {
        rootNode = FMovieSceneEvaluationTreeNode(Ar)
        childNodes = TEvaluationTreeEntryContainer(Ar) { Ar.readTArray { FMovieSceneEvaluationTreeNode(Ar) } }
    }

    constructor(rootNode: FMovieSceneEvaluationTreeNode, childNodes: TEvaluationTreeEntryContainer<FMovieSceneEvaluationTreeNode>) {
        this.rootNode = rootNode
        this.childNodes = childNodes
    }

    fun serialize(Ar: FArchiveWriter) {
        rootNode.serialize(Ar)
        childNodes.serialize(Ar) { items -> Ar.writeTArray(items) { it.serialize(Ar) } }
    }
}

class TMovieSceneEvaluationTree<DataType> : FMovieSceneEvaluationTree {
    var data: TEvaluationTreeEntryContainer<DataType>

    constructor(Ar: FArchive, init: () -> Array<DataType>) : super(Ar) {
        data = TEvaluationTreeEntryContainer(Ar, init)
    }

    fun serialize(Ar: FArchiveWriter, write: (Array<DataType>) -> Unit) {
        super.serialize(Ar)
        data.serialize(Ar, write)
    }

    constructor(
        rootNode: FMovieSceneEvaluationTreeNode,
        childNodes: TEvaluationTreeEntryContainer<FMovieSceneEvaluationTreeNode>,
        data: TEvaluationTreeEntryContainer<DataType>
    ) : super(rootNode, childNodes) {
        this.data = data
    }
}