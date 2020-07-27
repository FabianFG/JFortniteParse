package me.fungames.jfortniteparse.ue4.registry

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.registry.enums.EAssetRegistryDependencyType
import me.fungames.jfortniteparse.ue4.registry.objects.*
import me.fungames.jfortniteparse.ue4.registry.reader.FNameTableArchive
import java.io.File
import java.nio.ByteBuffer

@Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")
class AssetRegistry(originalAr: FArchive, val fileName: String) {
    val preallocatedAssetDataBuffer: Array<FAssetData>
    val preallocatedDependsNodeDataBuffer: Array<FDependsNode>
    val preallocatedPackageDataBuffer: Array<FAssetPackageData>

    constructor(bytes: ByteBuffer, fileName: String) : this(FByteArchive(bytes), fileName)
    constructor(bytes: ByteArray, fileName: String) : this(ByteBuffer.wrap(bytes), fileName)
    constructor(file: File) : this(file.readBytes(), file.name)

    init {
        val version = FAssetRegistryVersion(originalAr)

        @Suppress("LocalVariableName")
        val Ar = FNameTableArchive(originalAr)

        preallocatedAssetDataBuffer = Ar.readTArray { FAssetData(Ar) }

        val localNumDependsNodes = Ar.readInt32()
        preallocatedDependsNodeDataBuffer = Array(localNumDependsNodes) { FDependsNode() }

        val depCounts = mutableMapOf<EAssetRegistryDependencyType, Int>()

        fun serializeDependencyType(inDependencyType: EAssetRegistryDependencyType, assetIndex: Int) {
            for (i in 0 until (depCounts[inDependencyType] ?: 0)) {
                val index = Ar.readInt32()
                if (index < 0 || index >= localNumDependsNodes)
                    throw ParserException("Invalid DependencyType index")
                preallocatedDependsNodeDataBuffer[assetIndex].add(index, inDependencyType)
            }
        }

        for (dependsNodeIndex in 0 until localNumDependsNodes) {
            val assetIdentifier = FAssetIdentifier(Ar)

            depCounts.clear()

            depCounts[EAssetRegistryDependencyType.Hard] = Ar.readInt32()
            depCounts[EAssetRegistryDependencyType.Soft] = Ar.readInt32()
            depCounts[EAssetRegistryDependencyType.SearchableName] = Ar.readInt32()
            depCounts[EAssetRegistryDependencyType.SoftManage] = Ar.readInt32()
            depCounts[EAssetRegistryDependencyType.HardManage] =
                if (version < FAssetRegistryVersion.Type.AddedHardManage) 0 else Ar.readInt32()

            preallocatedDependsNodeDataBuffer[dependsNodeIndex].identifier = assetIdentifier
            preallocatedDependsNodeDataBuffer[dependsNodeIndex].reserve(depCounts)

            serializeDependencyType(EAssetRegistryDependencyType.Hard, dependsNodeIndex)
            serializeDependencyType(EAssetRegistryDependencyType.Soft, dependsNodeIndex)
            serializeDependencyType(EAssetRegistryDependencyType.SearchableName, dependsNodeIndex)
            serializeDependencyType(EAssetRegistryDependencyType.SoftManage, dependsNodeIndex)
            serializeDependencyType(EAssetRegistryDependencyType.HardManage, dependsNodeIndex)
            serializeDependencyType(EAssetRegistryDependencyType.Referencers, dependsNodeIndex)
        }

        val serializeHash = version < FAssetRegistryVersion.Type.AddedCookedMD5Hash
        preallocatedPackageDataBuffer = Ar.readTArray { FAssetPackageData(Ar, serializeHash) }
    }
}