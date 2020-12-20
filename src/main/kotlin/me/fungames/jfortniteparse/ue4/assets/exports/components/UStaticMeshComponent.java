package me.fungames.jfortniteparse.ue4.assets.exports.components;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.exports.UStaticMesh;
import me.fungames.jfortniteparse.ue4.objects.core.math.FColor;

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
    @UProperty(skipNext = 3)
    public Float StreamingDistanceMultiplier;
    //public List<FStaticMeshComponentLODInfo> LODData;
    //public List<FStreamingTextureBuildInfo> StreamingTextureData;
    //public FLightmassPrimitiveSettings LightmassSettings;
}
