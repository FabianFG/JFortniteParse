package me.fungames.jfortniteparse.ue4.assets.exports.components;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface;
import me.fungames.jfortniteparse.ue4.assets.objects.FLightingChannels;
import me.fungames.jfortniteparse.ue4.objects.core.math.FColor;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

public class ULightComponent extends ULightComponentBase {
    public Float Temperature;
    public Float MaxDrawDistance;
    public Float MaxDistanceFadeRange;
    public Boolean bUseTemperature;
    @Deprecated
    public Integer ShadowMapChannel;
    @Deprecated
    public Float MinRoughness;
    public Float SpecularScale;
    public Float ShadowResolutionScale;
    public Float ShadowBias;
    public Float ShadowSlopeBias;
    public Float ShadowSharpen;
    public Float ContactShadowLength;
    public Boolean ContactShadowLengthInWS;
    @Deprecated
    public Boolean InverseSquaredFalloff;
    public Boolean CastTranslucentShadows;
    public Boolean bCastShadowsFromCinematicObjectsOnly;
    public Boolean bAffectDynamicIndirectLighting;
    public Boolean bForceCachedShadowsForMovablePrimitives;
    public FLightingChannels LightingChannels;
    public Lazy<UMaterialInterface> LightFunctionMaterial;
    public FVector LightFunctionScale;
    public FPackageIndex /*TextureLightProfile*/ IESTexture;
    public Boolean bUseIESBrightness;
    public Float IESBrightnessScale;
    public Float LightFunctionFadeDistance;
    public Float DisabledBrightness;
    public Boolean bEnableLightShaftBloom;
    public Float BloomScale;
    public Float BloomThreshold;
    public Float BloomMaxBrightness;
    public FColor BloomTint;
    public Boolean bUseRayTracedDistanceFieldShadows;
    public Float RayStartOffsetDepthScale;
}
