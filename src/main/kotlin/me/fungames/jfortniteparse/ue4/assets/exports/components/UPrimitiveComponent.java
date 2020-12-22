package me.fungames.jfortniteparse.ue4.assets.exports.components;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.actors.AActor;
import me.fungames.jfortniteparse.ue4.assets.objects.FBodyInstance;
import me.fungames.jfortniteparse.ue4.objects.uobject.FMulticastScriptDelegate;
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
    public EHasCustomNavigableGeometry bHasCustomNavigableGeometry;
    public ECanBeCharacterBase CanCharacterStepUpOn;
    public FLightingChannels LightingChannels;
    public ERendererStencilMask CustomDepthStencilWriteMask;
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
    public FBodyInstance BodyInstance;
    public FMulticastScriptDelegate OnComponentHit;
    public FMulticastScriptDelegate OnComponentBeginOverlap;
    public FMulticastScriptDelegate OnComponentEndOverlap;
    public FMulticastScriptDelegate OnComponentWake;
    public FMulticastScriptDelegate OnComponentSleep;
    public FMulticastScriptDelegate OnBeginCursorOver;
    public FMulticastScriptDelegate OnEndCursorOver;
    public FMulticastScriptDelegate OnClicked;
    public FMulticastScriptDelegate OnReleased;
    public FMulticastScriptDelegate OnInputTouchBegin;
    public FMulticastScriptDelegate OnInputTouchEnd;
    public FMulticastScriptDelegate OnInputTouchEnter;
    public FMulticastScriptDelegate OnInputTouchLeave;
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

    public enum EHasCustomNavigableGeometry {
        No,
        Yes,
        EvenIfNotCollidable,
        DontExport
    }

    public enum ECanBeCharacterBase {
        ECB_No,
        ECB_Yes,
        ECB_Owner
    }

    @UStruct
    public static class FLightingChannels {
        public Boolean bChannel0;
        public Boolean bChannel1;
        public Boolean bChannel2;
    }

    public enum ERendererStencilMask {
        ERSM_Default,
        ERSM_255,
        ERSM_1,
        ERSM_2,
        ERSM_4,
        ERSM_8,
        ERSM_16,
        ERSM_32,
        ERSM_64,
        ERSM_128
    }
}
