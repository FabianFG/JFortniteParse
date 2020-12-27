package me.fungames.jfortniteparse.ue4.assets.exports.components;

import me.fungames.jfortniteparse.ue4.assets.UStruct;

public class ULocalLightComponent extends ULightComponent {
    public ELightUnits IntensityUnits;
    @Deprecated
    public Float Radius;
    public Float AttenuationRadius;
    public FLightmassPointLightSettings LightmassSettings;

    public enum ELightUnits {
        Unitless,
        Candelas,
        Lumens
    }

    @UStruct
    public static class FLightmassLightSettings {
        public Float IndirectLightingSaturation;
        public Float ShadowExponent;
        public Boolean bUseAreaShadowsForStationaryLight;
    }

    @UStruct
    public static class FLightmassPointLightSettings extends FLightmassLightSettings {
    }
}
