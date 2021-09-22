package me.fungames.jfortniteparse.ue4.assets.exports.mats

import me.fungames.jfortniteparse.ue4.reader.FArchive

open class FMaterial {
    fun serializeInlineShaderMap(Ar: FArchive) {
        val cooked = Ar.readBoolean()
        check(cooked)
        val valid = Ar.readBoolean()
        check(valid)
        val loadedShaderMap = FMaterialShaderMap()
        loadedShaderMap.deserialize(Ar)
    }
}

class FMaterialResource : FMaterial()

// MaterialShader.cpp
class FMaterialShaderMap : FShaderMapBase() {
    lateinit var shaderMapId: FMaterialShaderMapId

    override fun deserialize(Ar: FArchive) {
        val shaderMapId = FMaterialShaderMapId(Ar)
        super.deserialize(Ar)
    }
}

// Shader.h
open class FShaderMapBase {
    open fun deserialize(Ar: FArchive) {
        val useNewFormat = Ar.versions["ShaderMap.UseNewCookedFormat"]
        // region FMemoryImageResult::LoadFromArchive, MemoryImage.cpp
        val layoutParameters = if (useNewFormat) {
            FPlatformTypeLayoutParameters(Ar)
        } else null
        val frozenSize = Ar.readInt32()
        val frozenObject = Ar.read(frozenSize)

        if (useNewFormat) {
            FShaderMapPointerTable_LoadFromArchive(Ar, useNewFormat)
        }

        val numVTables = Ar.readUInt32()
        val numScriptNames = Ar.readUInt32()
        val numMinimalNames = Ar.readUInt32()
        println("$numVTables $numScriptNames $numMinimalNames")

        for (i in 0u until numVTables) {
            val typeNameHash = Ar.readUInt64()
            val numPatches = Ar.readUInt32()

            for (patchIndex in 0u until numPatches) {
                val vTableOffset = Ar.readUInt32()
                val offset = Ar.readUInt32()
            }
        }

        for (i in 0u until numScriptNames) {
            val name = Ar.readFName()
            println(name)
            val numPatches = Ar.readUInt32()

            for (patchIndex in 0u until numPatches) {
                val offset = Ar.readUInt32()
            }
        }

        for (i in 0u until numMinimalNames) {
            val name = Ar.readFName()
            val numPatches = Ar.readUInt32()

            for (patchIndex in 0u until numPatches) {
                val offset = Ar.readUInt32()
            }
        }
        // endregion

        if (!useNewFormat) {
            FShaderMapPointerTable_LoadFromArchive(Ar, useNewFormat)
        }

        val shareCode = Ar.readBoolean()
        val shaderPlatform = if (useNewFormat) {
            Ar.read() // EShaderPlatform
        } else 0

        if (shareCode) {
            val resourceHash = Ar.read(20)
            println()
        } else {
            TODO()
        }
    }

    // Shader.h
    private fun FShaderMapPointerTable_LoadFromArchive(Ar: FArchive, useNewFormat: Boolean) {
        if (useNewFormat) {
            FPointerTableBase_LoadFromArchive(Ar)
        }

        val numTypes = Ar.readInt32()
        val numVFTypes = Ar.readInt32()

        repeat(numTypes) { typeIndex ->
            var typeName = Ar.readUInt64() // actually FHashedName
        }

        repeat(numVFTypes) { vfTypeIndex ->
            var vfTypeName = Ar.readUInt64() // actually FHashedName
        }

        if (!useNewFormat) {
            FPointerTableBase_LoadFromArchive(Ar)
        }
    }

    // MemoryImage.cpp
    private fun FPointerTableBase_LoadFromArchive(Ar: FArchive) {
        val numDependencies = Ar.readInt32()
        repeat(numDependencies) {
            var nameHash = Ar.readUInt64()
            var savedLayoutSize = Ar.readUInt32()
            var savedLayoutHash = Ar.read(20)
        }
    }
}

class FPlatformTypeLayoutParameters(Ar: FArchive) {
    val maxFieldAlignment = Ar.readUInt32() // default: UInt.MAX_VALUE
    val flags = Ar.readUInt32() // default: 0
}

class FMaterialShaderMapId {
    constructor(Ar: FArchive) {
        val isLegacyPackage = Ar.ver < 260 // VER_UE4_PURGED_FMATERIAL_COMPILE_OUTPUTS
        if (!isLegacyPackage) {
            val qualityLevel = Ar.readInt32()
            val featureLevel = Ar.readInt32()
        } else {
            val legacyQualityLevel = Ar.readInt32()
        }

        // Cooked so can assume this is valid
        val cookedShaderMapIdHash = Ar.read(20)

        if (!isLegacyPackage) {
            val layoutParams = FPlatformTypeLayoutParameters(Ar)
        }
    }
}