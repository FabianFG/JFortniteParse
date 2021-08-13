package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface
import me.fungames.jfortniteparse.ue4.assets.objects.meshes.FStaticMaterial
import me.fungames.jfortniteparse.ue4.assets.objects.meshes.FStaticMeshLODResources
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.core.math.FBoxSphereBounds
import me.fungames.jfortniteparse.ue4.objects.core.math.FRotator
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.engine.FDistanceFieldVolumeData
import me.fungames.jfortniteparse.ue4.objects.engine.FStripDataFlags
import me.fungames.jfortniteparse.ue4.versions.*

internal const val MAX_STATIC_UV_SETS_UE4 = 8
internal const val MAX_STATIC_LODS_UE4 = 8

@OnlyAnnotated
class UStaticMesh : UStaticMesh_Properties() {
    lateinit var stripFlags: FStripDataFlags
    var bodySetup: Lazy<UObject>? = null // UBodySetup
    var navCollision: Lazy<UObject>? = null // UNavCollision
    lateinit var lightingGuid: FGuid
    lateinit var sockets: Array<Lazy<UObject>?>
    var lods = emptyArray<FStaticMeshLODResources>()
    var bounds = FBoxSphereBounds(FVector(0f, 0f, 0f), FVector(0f, 0f, 0f), 0f)
    var lodsShareStaticLighting = false
    var screenSize = Array(8) { 0f }
    var staticMaterials = emptyArray<FStaticMaterial>()
    var materials = emptyArray<Lazy<UMaterialInterface>>()

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        stripFlags = FStripDataFlags(Ar)
        val cooked = Ar.readBoolean()
        bodySetup = Ar.readObject()
        navCollision = if (Ar.ver >= VER_UE4_STATIC_MESH_STORE_NAV_COLLISION) Ar.readObject() else null

        if (!stripFlags.isEditorDataStripped()) {
            if (Ar.ver < VER_UE4_DEPRECATED_STATIC_MESH_THUMBNAIL_PROPERTIES_REMOVED) {
                FRotator(Ar) // dummyThumbnailAngle
                Ar.readFloat32() // dummyThumbnailDistance
            }
            Ar.readString() // highResSourceMeshName
            Ar.readUInt32() // highResSourceMeshCRC
        }
        lightingGuid = FGuid(Ar)
        sockets = Ar.readTArray { Ar.readObject() }
        if (!stripFlags.isEditorDataStripped()) {
            //TODO https://github.com/gildor2/UEViewer/blob/master/Unreal/UnMesh4.cpp#L2382
            throw ParserException("Static Mesh with Editor Data not implemented yet")
        }

        // serialize FStaticMeshRenderData
        if (cooked) {
            if (Ar.versions["StaticMesh.KeepMobileMinLODSettingOnDesktop"]) {
                // The serialization of this variable is cvar-dependent in UE4, so there's no clear way to understand
                // if it should be serialize in our code or not.
                Ar.readInt32() // MinMobileLODIdx
            }
            if (!cooked) {
                Ar.readTArray { Ar.readInt32() } // WedgeMap
                Ar.readTArray { Ar.readInt32() } // MaterialIndexToImportIndex
            }

            lods = Ar.readTArray { FStaticMeshLODResources(Ar) }

            if (Ar.game >= GAME_UE4(23))
                Ar.readUInt8() // NumInlinedLODs

            if (cooked) {
                if (Ar.ver >= VER_UE4_RENAME_CROUCHMOVESCHARACTERDOWN) {
                    var stripped = false
                    if (Ar.ver >= VER_UE4_RENAME_WIDGET_VISIBILITY) {
                        val stripFlags2 = FStripDataFlags(Ar)
                        stripped = stripFlags2.isDataStrippedForServer()
                        if (Ar.game >= GAME_UE4(21)) {
                            // 4.21 uses additional strip flag for distance field
                            val distanceFieldDataStripFlag: UByte = 1u
                            stripped = stripped or stripFlags2.isClassDataStripped(distanceFieldDataStripFlag)
                        }
                    }
                    if (!stripped) {
                        // serialize FDistanceFieldVolumeData for each LOD
                        for (i in lods.indices) {
                            val hasDistanceDataField = Ar.readBoolean()
                            if (hasDistanceDataField)
                                FDistanceFieldVolumeData(Ar) // VolumeData
                        }
                    }
                }
            }

            bounds = FBoxSphereBounds(Ar)

            // Note: bLODsShareStaticLighting field exists in all engine versions except UE4.15.
            if (Ar.versions["StaticMesh.HasLODsShareStaticLighting"])
                lodsShareStaticLighting = Ar.readBoolean()

            if (Ar.game < GAME_UE4(14))
                Ar.readBoolean() // bReducedBySimplygon

            if (FRenderingObjectVersion.get(Ar) < FRenderingObjectVersion.TextureStreamingMeshUVChannelData) {
                // StreamingTextureFactors
                // StreamingTextureFactor for each UV set
                for (i in 0 until MAX_STATIC_UV_SETS_UE4)
                    Ar.readFloat32()
                Ar.readFloat32() // MaxStreamingTextureFactor
            }

            if (cooked) {
                // ScreenSize for each LOD
                val maxNumLods = if (Ar.game >= GAME_UE4(9)) MAX_STATIC_LODS_UE4 else 4
                for (i in 0 until maxNumLods) {
                    if (Ar.game >= GAME_UE4(20))
                        Ar.readBoolean() // bFloatCooked
                    screenSize[i] = Ar.readFloat32()
                }
            }
        } // end of FStaticMeshRenderData

        if (cooked && Ar.game >= GAME_UE4(20)) {
            val hasOccluderData = Ar.readBoolean()
            if (hasOccluderData) {
                Ar.readTArray { FVector(Ar) } // Vertices
                Ar.readTArray { Ar.readUInt16() } // Indices
            }
        }

        if (Ar.game >= GAME_UE4(14)) {
            // Serialize following data to obtain material references for UE4.14+.
            // Don't bother serializing anything beyond this point in earlier versions.
            // Note: really, UE4 uses VER_UE4_SPEEDTREE_STATICMESH
            val hasSpeedTreeWind = Ar.readBoolean()
            if (hasSpeedTreeWind) {
                //TODO - FSpeedTreeWind serialization
                //Ignore remaining data
            } else {
                if (FEditorObjectVersion.get(Ar) >= FEditorObjectVersion.RefactorMeshEditorMaterials) {
                    // UE4.14+ - "Materials" are deprecated, added StaticMaterials
                    staticMaterials = Ar.readTArray { FStaticMaterial(Ar) }
                }
            }
        }

        materials = staticMaterials.mapNotNull { it.materialInterface }.toTypedArray()

        //Drop remaining SpeedTree data
        if (validPos > 0) Ar.seek(validPos)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        throw ParserException("Serializing UStaticMesh not supported")
    }
}