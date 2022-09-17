package me.fungames.jfortniteparse.ue4.registry

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.registry.objects.FAssetData
import me.fungames.jfortniteparse.ue4.registry.objects.FAssetPackageData
import me.fungames.jfortniteparse.ue4.registry.objects.FAssetRegistryVersion
import me.fungames.jfortniteparse.ue4.registry.objects.FDependsNode
import me.fungames.jfortniteparse.ue4.registry.reader.FAssetRegistryArchive
import me.fungames.jfortniteparse.ue4.registry.reader.FAssetRegistryHeader
import me.fungames.jfortniteparse.ue4.registry.reader.FAssetRegistryReader
import me.fungames.jfortniteparse.ue4.registry.reader.FNameTableArchiveReader
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
        val header = FAssetRegistryHeader(originalAr)
        val version = header.version
        val Ar = when {
            version < FAssetRegistryVersion.Type.RemovedMD5Hash -> throw ParserException("Cannot read states before this version")
            version < FAssetRegistryVersion.Type.FixedTags -> FNameTableArchiveReader(originalAr, header)
            else -> FAssetRegistryReader(originalAr, header)
        }

        preallocatedAssetDataBuffer = Ar.readTArray { FAssetData(Ar) }

        if (version < FAssetRegistryVersion.Type.AddedDependencyFlags) {
            val localNumDependsNodes = Ar.readInt32()
            preallocatedDependsNodeDataBuffer = Array(localNumDependsNodes) { FDependsNode(it) }
            if (localNumDependsNodes > 0) {
                loadDependenciesBeforeFlags(Ar)
            }
        } else {
            val dependencySectionSize = Ar.readInt64()
            val dependencySectionEnd = Ar.pos() + dependencySectionSize.toInt()
            val localNumDependsNodes = Ar.readInt32()
            preallocatedDependsNodeDataBuffer = Array(localNumDependsNodes) { FDependsNode(it) }
            if (localNumDependsNodes > 0) {
                loadDependencies(Ar)
            }
            Ar.seek(dependencySectionEnd)
        }

        preallocatedPackageDataBuffer = Ar.readTArray { FAssetPackageData(Ar) }
    }

    private fun loadDependencies(Ar: FAssetRegistryArchive) {
        for (dependsNode in preallocatedDependsNodeDataBuffer) {
            dependsNode.serializeLoad(Ar, preallocatedDependsNodeDataBuffer)
        }
    }

    private fun loadDependenciesBeforeFlags(Ar: FAssetRegistryArchive) {
        for (dependsNode in preallocatedDependsNodeDataBuffer) {
            dependsNode.serializeLoadBeforeFlags(Ar, preallocatedDependsNodeDataBuffer)
        }
    }
}