package me.fungames.jfortniteparse.ue4.assets.exports.mats;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.enums.EBlendMode;
import me.fungames.jfortniteparse.ue4.assets.enums.EMaterialShadingModel;
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture;
import me.fungames.jfortniteparse.ue4.assets.objects.mats.FMaterialCachedParameters;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class UMaterialInstance extends UMaterialInterface {
    public FPackageIndex /*PhysicalMaterial*/ PhysMaterial;
    @UProperty(arrayDim = 8)
    public FPackageIndex[] /*PhysicalMaterial[]*/ PhysicalMaterialMap;
    public FPackageIndex /*MaterialInterface*/ Parent;
    public Boolean bHasStaticPermutationResource;
    public Boolean bOverrideSubsurfaceProfile;
    public List<FScalarParameterValue> ScalarParameterValues;
    public List<FVectorParameterValue> VectorParameterValues;
    public List<FTextureParameterValue> TextureParameterValues;
    public List<FRuntimeVirtualTextureParameterValue> RuntimeVirtualTextureParameterValues;
    public List<FFontParameterValue> FontParameterValues;
    public FMaterialInstanceBasePropertyOverrides BasePropertyOverrides;
    public FStaticParameterSet StaticParameters;
    public FMaterialCachedParameters CachedLayerParameters;
    public List<FPackageIndex /*Object*/> CachedReferencedTextures;

    @UStruct
    public static class FMaterialParameterInfo {
        public FName Name;
        public EMaterialParameterAssociation Association;
        public Integer Index;
    }

    public enum EMaterialParameterAssociation {
        LayerParameter,
        BlendParameter,
        GlobalParameter
    }

    @UStruct
    public static class FScalarParameterValue {
        public FMaterialParameterInfo ParameterInfo;
        public Float ParameterValue;
        public FGuid ExpressionGUID;
    }

    @UStruct
    public static class FVectorParameterValue {
        public FMaterialParameterInfo ParameterInfo;
        public FLinearColor ParameterValue;
        public FGuid ExpressionGUID;
    }

    @UStruct
    public static class FTextureParameterValue {
        public FMaterialParameterInfo ParameterInfo;
        public Lazy<UTexture> ParameterValue;
        public FGuid ExpressionGUID;
    }

    @UStruct
    public static class FRuntimeVirtualTextureParameterValue {
        public FMaterialParameterInfo ParameterInfo;
        public FPackageIndex /*RuntimeVirtualTexture*/ ParameterValue;
        public FGuid ExpressionGUID;
    }

    @UStruct
    public static class FFontParameterValue {
        public FMaterialParameterInfo ParameterInfo;
        public FPackageIndex /*Font*/ FontValue;
        public Integer FontPage;
        public FGuid ExpressionGUID;
    }

    @UStruct
    public static class FMaterialInstanceBasePropertyOverrides {
        public Boolean bOverride_OpacityMaskClipValue;
        public Boolean bOverride_BlendMode;
        public Boolean bOverride_ShadingModel;
        public Boolean bOverride_DitheredLODTransition;
        public Boolean bOverride_CastDynamicShadowAsMasked;
        public Boolean bOverride_TwoSided;
        public Boolean TwoSided;
        public Boolean DitheredLODTransition;
        public Boolean bCastDynamicShadowAsMasked;
        public EBlendMode BlendMode;
        public EMaterialShadingModel ShadingModel;
        public Float OpacityMaskClipValue;
    }

    @UStruct
    public static class FStaticParameterSet {
        public List<FStaticSwitchParameter> StaticSwitchParameters;
        public List<FStaticComponentMaskParameter> StaticComponentMaskParameters;
        public List<FStaticTerrainLayerWeightParameter> TerrainLayerWeightParameters;
        public List<FStaticMaterialLayersParameter> MaterialLayersParameters;
    }

    @UStruct
    public static class FStaticParameterBase {
        public FMaterialParameterInfo ParameterInfo;
        public Boolean bOverride;
        public FGuid ExpressionGUID;
    }

    @UStruct
    public static class FStaticSwitchParameter extends FStaticParameterBase {
        public Boolean Value;
    }

    @UStruct
    public static class FStaticComponentMaskParameter extends FStaticParameterBase {
        public Boolean R;
        public Boolean G;
        public Boolean B;
        public Boolean A;
    }

    @UStruct
    public static class FStaticTerrainLayerWeightParameter extends FStaticParameterBase {
        public Integer WeightmapIndex;
        public Boolean bWeightBasedBlend;
    }

    @UStruct
    public static class FStaticMaterialLayersParameter extends FStaticParameterBase {
        public FMaterialLayersFunctions Value;
    }

    @UStruct
    public static class FMaterialLayersFunctions {
        public List<FPackageIndex /*MaterialFunctionInterface*/> Layers;
        public List<FPackageIndex /*MaterialFunctionInterface*/> Blends;
        public List<Boolean> LayerStates;
        public String KeyString;
    }
}
