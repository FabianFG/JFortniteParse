package me.fungames.jfortniteparse.ue4.registry

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.registry.objects.FAssetData
import me.fungames.jfortniteparse.ue4.registry.objects.FAssetPackageData
import me.fungames.jfortniteparse.ue4.registry.objects.FAssetRegistryVersion
import me.fungames.jfortniteparse.ue4.registry.objects.FDependsNode
import me.fungames.jfortniteparse.ue4.registry.reader.FNameTableArchive
import java.io.File
import java.nio.ByteBuffer

class AssetRegistry(originalAr: FArchive, val fileName: String) {
    val preallocatedAssetDataBuffer: Array<FAssetData>
    val preallocatedDependsNodeDataBuffer: Array<FDependsNode>
    val preallocatedPackageDataBuffer: Array<FAssetPackageData>

    constructor(bytes: ByteBuffer, fileName: String) : this(FByteArchive(bytes), fileName)
    constructor(bytes: ByteArray, fileName: String) : this(ByteBuffer.wrap(bytes), fileName)
    constructor(file: File) : this(file.readBytes(), file.name)

    init {
        val version = FAssetRegistryVersion(originalAr)
        val Ar = FNameTableArchive(originalAr)

        preallocatedAssetDataBuffer = Ar.readTArray { FAssetData(Ar) }

        if (version < FAssetRegistryVersion.Type.AddedDependencyFlags) {
            val localNumDependsNodes = Ar.readInt32()
            preallocatedDependsNodeDataBuffer = Array(localNumDependsNodes) { FDependsNode() }
            if (localNumDependsNodes > 0) {
                loadDependenciesBeforeFlags(Ar, version)
            }
        } else {
            val dependencySectionSize = Ar.readInt64()
            val dependencySectionEnd = Ar.pos() + dependencySectionSize.toInt()
            val localNumDependsNodes = Ar.readInt32()
            preallocatedDependsNodeDataBuffer = Array(localNumDependsNodes) { FDependsNode() }
            if (localNumDependsNodes > 0) {
                loadDependencies(Ar)
            }
            Ar.seek(dependencySectionEnd)
        }

        val serializeHash = version < FAssetRegistryVersion.Type.AddedCookedMD5Hash
        preallocatedPackageDataBuffer = Ar.readTArray { FAssetPackageData(Ar, serializeHash) }
    }

    private fun loadDependencies(Ar: FArchive) {
        fun getNodeFromSerializeIndex(index: Int): FDependsNode? {
            if (index < 0 || preallocatedDependsNodeDataBuffer.size <= index) {
                return null
            }
            return preallocatedDependsNodeDataBuffer[index]
        }

        for (dependsNode in preallocatedDependsNodeDataBuffer) {
            dependsNode.serializeLoad(Ar, ::getNodeFromSerializeIndex)
        }
    }

    private fun loadDependenciesBeforeFlags(Ar: FArchive, version: FAssetRegistryVersion) {
        for (dependsNode in preallocatedDependsNodeDataBuffer) {
            dependsNode.serializeLoadBeforeFlags(Ar, version, preallocatedDependsNodeDataBuffer, preallocatedDependsNodeDataBuffer.size)
        }
    }
}