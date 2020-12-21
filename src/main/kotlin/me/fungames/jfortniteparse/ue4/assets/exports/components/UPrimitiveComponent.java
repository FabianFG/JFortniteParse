package me.fungames.jfortniteparse.ue4.assets.exports.components;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.exports.actors.AActor;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class UPrimitiveComponent extends USceneComponent {
    public Float MinDrawDistance;
    public Float LDMaxDrawDistance;
    public Float CachedMaxDrawDistance;
    public ESceneDepthPriorityGroup DepthPriorityGroup;
    /*public ESceneDepthPriorityGroup ViewOwnerDepthPriorityGroup;
    public EIndirectLightingCacheQuality IndirectLightingCacheQuality;*/
    @UProperty(skipPrevious = 2)
    public ELightmapType LightmapType;
    public Boolean bUseMaxLODAsImposter;
    public Boolean bBatchImpostersAsInstances;
    public Boolean bNeverDistanceCull;
    public Boolean bAlwaysCreatePhysicsState;
    public Boolean bGenerateOverlapEvents;
    public Boolean bMultiBodyOverlap;
    public Boolean bTraceComplexOnMove;
    public Boolean bReturnMaterialOnMove;
    public Boolean bUseViewOwnerDepthPriorityGroup;
    public Boolean bAllowCullDistanceVolume;
    public Boolean bHasMotionBlurVelocityMeshes;
    public Boolean bVisibleInReflectionCaptures;
    public Boolean bVisibleInRealTimeSkyCaptures;
    public Boolean bVisibleInRayTracing;
    public Boolean bRenderInMainPass;
    public Boolean bRenderInDepthPass;
    public Boolean bReceivesDecals;
    public Boolean bOwnerNoSee;
    public Boolean bOnlyOwnerSee;
    public Boolean bTreatAsBackgroundForOcclusion;
    public Boolean bUseAsOccluder;
    public Boolean bSelectable;
    public Boolean bForceMipStreaming;
    public Boolean bHasPerInstanceHitProxies;
    public Boolean CastShadow;
    public Boolean bAffectDynamicIndirectLighting;
    public Boolean bAffectDistanceFieldLighting;
    public Boolean bCastDynamicShadow;
    public Boolean bCastStaticShadow;
    public Boolean bCastVolumetricTranslucentShadow;
    public Boolean bCastContactShadow;
    public Boolean bSelfShadowOnly;
    public Boolean bCastFarShadow;
    public Boolean bCastInsetShadow;
    public Boolean bCastCinematicShadow;
    public Boolean bCastHiddenShadow;
    public Boolean bCastShadowAsTwoSided;
    public Boolean bLightAsIfStatic;
    public Boolean bLightAttachmentsAsGroup;
    public Boolean bExcludeFromLightAttachmentGroup;
    public Boolean bReceiveMobileCSMShadows;
    public Boolean bSingleSampleShadowFromStationaryLights;
    public Boolean bIgnoreRadialImpulse;
    public Boolean bIgnoreRadialForce;
    public Boolean bApplyImpulseOnDamage;
    public Boolean bReplicatePhysicsToAutonomousProxy;
    public Boolean bFillCollisionUnderneathForNavmesh;
    public Boolean AlwaysLoadOnClient;
    public Boolean AlwaysLoadOnServer;
    public Boolean bUseEditorCompositing;
    public Boolean bRenderCustomDepth;
    /*public EHasCustomNavigableGeometry bHasCustomNavigableGeometry;
    public ECanBeCharacterBase CanCharacterStepUpOn;
    public FLightingChannels LightingChannels;
    public ERendererStencilMask CustomDepthStencilWriteMask;*/
    @UProperty(skipPrevious = 4)
    public Integer CustomDepthStencilValue;
    /*public FCustomPrimitiveData CustomPrimitiveData;
    public FCustomPrimitiveData CustomPrimitiveDataInternal;*/
    @UProperty(skipPrevious = 2)
    public Integer TranslucencySortPriority;
    public Integer VisibilityId;
    public List<FPackageIndex /*RuntimeVirtualTexture*/> RuntimeVirtualTextures;
    public Byte VirtualTextureLodBias;
    public Byte VirtualTextureCullMips;
    public Byte VirtualTextureMinCoverage;
    //public ERuntimeVirtualTextureMainPassType VirtualTextureRenderPassType;
    @UProperty(skipPrevious = 1)
    public Float LpvBiasMultiplier;
    public Float BoundsScale;
    public List<Lazy<AActor>> MoveIgnoreActors;
    public List<FPackageIndex /*PrimitiveComponent*/> MoveIgnoreComponents;
    /*public FBodyInstance BodyInstance;
    public FScriptMulticastDelegate OnComponentHit;
    public FScriptMulticastDelegate OnComponentBeginOverlap;
    public FScriptMulticastDelegate OnComponentEndOverlap;
    public FScriptMulticastDelegate OnComponentWake;
    public FScriptMulticastDelegate OnComponentSleep;
    public FScriptMulticastDelegate OnBeginCursorOver;
    public FScriptMulticastDelegate OnEndCursorOver;
    public FScriptMulticastDelegate OnClicked;
    public FScriptMulticastDelegate OnReleased;
    public FScriptMulticastDelegate OnInputTouchBegin;
    public FScriptMulticastDelegate OnInputTouchEnd;
    public FScriptMulticastDelegate OnInputTouchEnter;
    public FScriptMulticastDelegate OnInputTouchLeave;*/
    @UProperty(skipPrevious = 14)
    public Lazy<UPrimitiveComponent> LODParentPrimitive;

    public enum ESceneDepthPriorityGroup {
        SDPG_World,
        SDPG_Foreground
    }

    public enum ELightmapType {
        Default,
        ForceSurface,
        ForceVolumetric
    }
}
