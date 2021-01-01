package me.fungames.jfortniteparse.ue4.assets.exports.components;

import me.fungames.jfortniteparse.ue4.objects.core.math.FColor;
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid;

public class ULightComponentBase extends USceneComponent {
    public FGuid LightGuid;
    @Deprecated
    public Float Brightness;
    public Float Intensity;
    public FColor LightColor;
    public Boolean bAffectsWorld;
    public Boolean CastShadows;
    public Boolean CastStaticShadows;
    public Boolean CastDynamicShadows;
    public Boolean bAffectTranslucentLighting;
    public Boolean bTransmission;
    public Boolean bCastVolumetricShadow;
    public Boolean bCastDeepShadow;
    public Boolean bCastRaytracedShadow;
    public Boolean bAffectReflection;
    public Boolean bAffectGlobalIllumination;
    //public Float DeepShadowLayerDistribution; // in UE4 src but not in Fortnite
    public Float IndirectLightingIntensity;
    public Float VolumetricScatteringIntensity;
    public Integer SamplesPerPixel;
}
