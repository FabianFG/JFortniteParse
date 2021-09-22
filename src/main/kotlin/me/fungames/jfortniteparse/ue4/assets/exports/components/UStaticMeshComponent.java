package me.fungames.jfortniteparse.ue4.assets.exports.components;

import kotlin.Lazy;
import kotlin.UInt;
import me.fungames.jfortniteparse.Globals;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UStaticMesh;
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive;
import me.fungames.jfortniteparse.ue4.objects.core.math.FColor;
import me.fungames.jfortniteparse.ue4.reader.FArchive;
import me.fungames.jfortniteparse.ue4.versions.VersionsKt;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UStaticMeshComponent extends UMeshComponent {
    public Integer ForcedLodModel;
    public Integer PreviousLODLevel;
    public Integer MinLOD;
    public Integer SubDivisionStepSize;
    public Lazy<UStaticMesh> StaticMesh;
    public FColor WireframeColorOverride;
    public Boolean bEvaluateWorldPositionOffset;
    public Boolean bOverrideWireframeColor;
    public Boolean bOverrideMinLod;
    public Boolean bOverrideNavigationExport;
    public Boolean bForceNavigationObstacle;
    public Boolean bDisallowMeshPaintPerInstance;
    public Boolean bIgnoreInstanceForTextureStreaming;
    public Boolean bOverrideLightMapRes;
    public Boolean bCastDistanceFieldIndirectShadow;
    public Boolean bOverrideDistanceFieldSelfShadowBias;
    public Boolean bUseSubDivisions;
    public Boolean bUseDefaultCollision;
    public Boolean bReverseCulling;
    public Integer OverriddenLightMapRes;
    public Float DistanceFieldIndirectShadowMinVisibility;
    public Float DistanceFieldSelfShadowBias;
    public Float StreamingDistanceMultiplier;
    //public List<FStaticMeshComponentLODInfo> LODData;
    @UProperty(skipPrevious = 1)
    public List<FStreamingTextureBuildInfo> StreamingTextureData;
    public FLightmassPrimitiveSettings LightmassSettings;

    @Override
    public void deserialize(@NotNull FAssetArchive Ar, int validPos) {
        super.deserialize(Ar, validPos);
        int lodDataNum = Ar.readInt32();
        if (lodDataNum > 0) {
            Globals.LOG_JFP.debug("Skipping {} LODData entries", lodDataNum);
            Ar.seek(validPos);
        }
    }

    @UStruct
    public static class FStreamingTextureBuildInfo {
        public UInt PackedRelativeBox;
        public Integer TextureLevelIndex;
        public Float TexelFactor;
    }

    @UStruct
    public static class FLightmassPrimitiveSettings {
        public Boolean bUseTwoSidedLighting;
        public Boolean bShadowIndirectOnly;
        public Boolean bUseEmissiveForStaticLighting;
        public Boolean bUseVertexNormalForHemisphereGather;
        public Float EmissiveLightFalloffExponent;
        public Float EmissiveLightExplicitInfluenceRadius;
        public Float EmissiveBoost;
        public Float DiffuseBoost;
        public Float FullyOccludedSamplesFraction;

        public FLightmassPrimitiveSettings() { }

        public FLightmassPrimitiveSettings(FArchive Ar) {
            bUseTwoSidedLighting = Ar.readBoolean();
            bShadowIndirectOnly = Ar.readBoolean();
            FullyOccludedSamplesFraction = Ar.readFloat32();
            bUseEmissiveForStaticLighting = Ar.readBoolean();
            if (Ar.getVer() >= VersionsKt.VER_UE4_NEW_LIGHTMASS_PRIMITIVE_SETTING) {
                bUseVertexNormalForHemisphereGather = Ar.readBoolean();
            }
            EmissiveLightFalloffExponent = Ar.readFloat32();
            EmissiveLightExplicitInfluenceRadius = Ar.readFloat32();
            EmissiveBoost = Ar.readFloat32();
            DiffuseBoost = Ar.readFloat32();
        }
    }
}
