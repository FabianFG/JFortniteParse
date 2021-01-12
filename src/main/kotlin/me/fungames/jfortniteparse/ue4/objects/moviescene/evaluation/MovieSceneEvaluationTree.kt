package me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.objects.core.math.TRange
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FEvaluationTreeEntryHandle : UClass {
    var entryIndex: Int

    constructor(Ar: FArchive) {
        super.init(Ar)
        entryIndex = Ar.readInt32()
        super.complete(Ar)
    }

    constructor(entryIndex: Int) {
        this.entryIndex = entryIndex
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(entryIndex)
        super.completeWrite(Ar)
    }
}

class TEvaluationTreeEntryContainer<ElementType> : UClass {
    var entries: Array<FEntry>
    var items: Array<ElementType>

    constructor(Ar: FArchive, init: () -> Array<ElementType>) {
        super.init(Ar)
        entries = Ar.readTArray { FEntry(Ar) }
        items = init()
        super.complete(Ar)
    }

    constructor(entries: Array<FEntry>, items: Array<ElementType>) {
        this.entries = entries
        this.items = items
    }

    fun serialize(Ar: FArchiveWriter, write: (Array<ElementType>) -> Unit) {
        super.initWrite(Ar)
        Ar.writeTArray(entries) { it.serialize(Ar) }
        write(items)
        super.completeWrite(Ar)
    }

    class FEntry : UClass {
        /** The index into Items of the first item */
        var startIndex: Int
        /** The number of currently valid items */
        var size: Int
        /** The total capacity of allowed items before reallocating */
        var capacity: Int

        constructor(Ar: FArchive) {
            super.init(Ar)
            startIndex = Ar.readInt32()
            size = Ar.readInt32()
            capacity = Ar.readInt32()
            super.complete(Ar)
        }

        constructor(startIndex: Int, size: Int, capacity: Int) {
            this.startIndex = startIndex
            this.size = size
            this.capacity = capacity
        }

        fun serialize(Ar: FArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeInt32(startIndex)
            Ar.writeInt32(size)
            Ar.writeInt32(capacity)
            super.completeWrite(Ar)
        }
    }
}

class FMovieSceneEvaluationTreeNodeHandle : UClass {
    var childrenHandle: FEvaluationTreeEntryHandle
    var index: Int

    constructor(Ar: FArchive) {
        super.init(Ar)
        childrenHandle = FEvaluationTreeEntryHandle(Ar)
        index = Ar.readInt32()
        super.complete(Ar)
    }

    constructor(childrenHandle: FEvaluationTreeEntryHandle, index: Int) {
        this.childrenHandle = childrenHandle
        this.index = index
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        childrenHandle.serialize(Ar)
        Ar.writeInt32(index)
        super.completeWrite(Ar)
    }
}

class FMovieSceneEvaluationTreeNode : UClass {
    var range: TRange<Int>
    var parent: FMovieSceneEvaluationTreeNodeHandle
    var childrenId: FEvaluationTreeEntryHandle
    var dataId: FEvaluationTreeEntryHandle

    constructor(Ar: FArchive) {
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

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        range.serialize(Ar) { Ar.writeInt32(it) }
        parent.serialize(Ar)
        childrenId.serialize(Ar)
        dataId.serialize(Ar)
        super.completeWrite(Ar)
    }
}

open class FMovieSceneEvaluationTree : UClass {
    var rootNode: FMovieSceneEvaluationTreeNode
    var childNodes: TEvaluationTreeEntryContainer<FMovieSceneEvaluationTreeNode>

    constructor(Ar: FArchive) {
        super.init(Ar)
        rootNode = FMovieSceneEvaluationTreeNode(Ar)
        childNodes = TEvaluationTreeEntryContainer(Ar) { Ar.readTArray { FMovieSceneEvaluationTreeNode(Ar) } }
        super.complete(Ar)
    }

    constructor(rootNode: FMovieSceneEvaluationTreeNode, childNodes: TEvaluationTreeEntryContainer<FMovieSceneEvaluationTreeNode>) {
        this.rootNode = rootNode
        this.childNodes = childNodes
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        rootNode.serialize(Ar)
        childNodes.serialize(Ar) { items -> Ar.writeTArray(items) { it.serialize(Ar) } }
        super.completeWrite(Ar)
    }
}

class TMovieSceneEvaluationTree<DataType> : FMovieSceneEvaluationTree {
    var data: TEvaluationTreeEntryContainer<DataType>

    constructor(Ar: FArchive, init: () -> Array<DataType>) : super(Ar) {
        data = TEvaluationTreeEntryContainer(Ar, init)
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter, write: (Array<DataType>) -> Unit) {
        super.serialize(Ar)
        data.serialize(Ar, write)
        super.completeWrite(Ar)
    }

    constructor(
        rootNode: FMovieSceneEvaluationTreeNode,
        childNodes: TEvaluationTreeEntryContainer<FMovieSceneEvaluationTreeNode>,
        data: TEvaluationTreeEntryContainer<DataType>
    ) : super(rootNode, childNodes) {
        this.data = data
    }
}