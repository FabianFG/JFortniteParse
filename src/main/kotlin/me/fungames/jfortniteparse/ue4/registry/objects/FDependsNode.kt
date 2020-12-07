package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.util.divideAndRoundUp
import me.fungames.jfortniteparse.util.ref
import java.util.*
import kotlin.collections.ArrayList
import kotlin.jvm.internal.Ref.ObjectRef

class FDependsNode {
    companion object {
        const val packageFlagWidth = 3
        const val packageFlagSetWidth = 1 shr packageFlagWidth
        const val manageFlagWidth = 1
        const val manageFlagSetWidth = 1 shr manageFlagWidth
    }

    /** The name of the package/object this node represents */
    lateinit var identifier: FAssetIdentifier
    var packageDependencies: MutableList<FDependsNode>? = null
        private set
    var nameDependencies: MutableList<FDependsNode>? = null
        private set
    var manageDependencies: MutableList<FDependsNode>? = null
        private set
    var referencers: MutableList<FDependsNode>? = null
        private set
    var packageFlags: BitSet? = null
        private set
    var manageFlags: BitSet? = null
        private set

    fun serializeLoad(Ar: FArchive, getNodeFromSerializeIndex: (Int) -> FDependsNode?) {
        identifier = FAssetIdentifier(Ar)

        fun readDependencies(outDependencies: ObjectRef<MutableList<FDependsNode>?>, outFlagBits: ObjectRef<BitSet?>?, flagSetWidth: Int) {
            var inFlagBits: BitSet? = null
            val pointerDependencies = mutableListOf<FDependsNode>()
            val sortIndexes = mutableListOf<Int>()
            var numFlagBits = 0

            val n = Ar.readInt32()
            val inDependencies = Ar.readTArray(n) { Ar.readInt32() }
            val numDependencies = inDependencies.size
            if (outFlagBits != null) {
                numFlagBits = flagSetWidth * numDependencies
                val numFlagWords = numFlagBits.toUInt().divideAndRoundUp(32u)
                inFlagBits = BitSet.valueOf(Ar.read(numFlagWords.toInt()))
            }

            for (serializeIndex in inDependencies) {
                val dependsNode = getNodeFromSerializeIndex(serializeIndex)
                    ?: throw ParserException("Invalid index of preallocatedDependsNodeDataBuffer")
                pointerDependencies.add(dependsNode)
            }

            for (index in 0 until numDependencies) {
                sortIndexes.add(index)
            }

            //sortIndexes.sortBy { pointerDependencies[it] }

            outDependencies.element = ArrayList(numDependencies)
            for (sortIndex in sortIndexes) {
                outDependencies.element!!.add(pointerDependencies[sortIndex])
            }
            if (outFlagBits != null) {
                outFlagBits.element = BitSet(numFlagBits)
                for (writeIndex in 0 until numDependencies) {
                    val readIndex = sortIndexes[writeIndex]
                    val start = writeIndex * flagSetWidth
                    for (index in start until start + flagSetWidth) {
                        outFlagBits.element!!.set(index, inFlagBits!!.get(readIndex * flagSetWidth + index))
                    }
                }
            }
        }

        val packageDependenciesRef = packageDependencies.ref()
        val packageFlagsRef = packageFlags.ref()
        val nameDependenciesRef = nameDependencies.ref()
        val manageDependenciesRef = manageDependencies.ref()
        val manageFlagsRef = manageFlags.ref()
        val referencersRef = referencers.ref()
        readDependencies(packageDependenciesRef, packageFlagsRef, packageFlagSetWidth)
        readDependencies(nameDependenciesRef, null, 0)
        readDependencies(manageDependenciesRef, manageFlagsRef, manageFlagSetWidth)
        readDependencies(referencersRef, null, 0)
        packageDependencies = packageDependenciesRef.element
        packageFlags = packageFlagsRef.element
        nameDependencies = nameDependenciesRef.element
        manageDependencies = manageDependenciesRef.element
        manageFlags = manageFlagsRef.element
        referencers = referencersRef.element
    }

    fun serializeLoadBeforeFlags(Ar: FArchive, version: FAssetRegistryVersion, preallocatedDependsNodeDataBuffer: Array<FDependsNode>, numDependsNodes: Int) {
        identifier = FAssetIdentifier(Ar)

        val numHard = Ar.readInt32()
        val numSoft = Ar.readInt32()
        val numName = Ar.readInt32()
        val numSoftManage = Ar.readInt32()
        val numHardManage = if (version >= FAssetRegistryVersion.Type.AddedHardManage) Ar.readInt32() else 0
        val numReferencers = Ar.readInt32()

        // Empty dependency arrays and reserve space
        packageDependencies = ArrayList(numHard + numSoft)
        nameDependencies = ArrayList(numName)
        manageDependencies = ArrayList(numSoftManage + numHardManage)
        referencers = ArrayList(numReferencers)

        fun serializeNodeArray(num: Int, outNodes: ObjectRef<MutableList<FDependsNode>?>) {
            for (i in 0 until num) {
                val index = Ar.readInt32()
                if (index < 0 || index >= numDependsNodes) {
                    throw ParserException("Invalid DependencyType index")
                }
                val dependsNode = preallocatedDependsNodeDataBuffer[index]
                outNodes.element!!.add(index, dependsNode)
            }
        }

        // Read the bits for each type, but don't write anything if serializing that type isn't allowed
        val packageDependenciesRef = packageDependencies.ref()
        val nameDependenciesRef = nameDependencies.ref()
        val manageDependenciesRef = manageDependencies.ref()
        val referencersRef = referencers.ref()
        serializeNodeArray(numHard, packageDependenciesRef)
        serializeNodeArray(numSoft, packageDependenciesRef)
        serializeNodeArray(numName, nameDependenciesRef)
        serializeNodeArray(numSoftManage, manageDependenciesRef)
        serializeNodeArray(numHardManage, manageDependenciesRef)
        serializeNodeArray(numReferencers, referencersRef)
        packageDependencies = packageDependenciesRef.element
        nameDependencies = nameDependenciesRef.element
        manageDependencies = manageDependenciesRef.element
        referencers = referencersRef.element
    }
}