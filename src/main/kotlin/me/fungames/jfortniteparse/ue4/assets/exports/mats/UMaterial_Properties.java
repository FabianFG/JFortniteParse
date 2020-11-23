package me.fungames.jfortniteparse.ue4.assets.exports.mats;

import kotlin.UByte;
import kotlin.UInt;
import kotlin.UShort;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.enums.EBlendMode;
import me.fungames.jfortniteparse.ue4.assets.enums.EMaterialShadingModel;
import me.fungames.jfortniteparse.ue4.assets.objects.mats.FMaterialCachedParameters;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid;
import me.fungames.jfortniteparse.ue4.objects.engine.FColorMaterialInput;
import me.fungames.jfortniteparse.ue4.objects.engine.FScalarMaterialInput;
import me.fungames.jfortniteparse.ue4.objects.engine.FVectorMaterialInput;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class UMaterial_Properties extends UMaterialInterface {
    public FPackageIndex /*PhysicalMaterial*/ PhysMaterial;
    public FPackageIndex /*PhysicalMaterialMask*/ PhysMaterialMask;
    @UProperty(arrayDim = 8)
    public FPackageIndex[] /*PhysicalMaterial[]*/ PhysicalMaterialMap;
    public FScalarMaterialInput Metallic;
    public FScalarMaterialInput Specular;
    public FScalarMaterialInput Anisotropy;
    public FVectorMaterialInput Normal;
    public FVectorMaterialInput Tangent;
    public FColorMaterialInput EmissiveColor;
    public EMaterialDomain MaterialDomain;
    public EBlendMode BlendMode;
    //public EDecalBlendMode DecalBlendMode;
    //public EMaterialDecalResponse MaterialDecalResponse;
    @UProperty(skipPrevious = 2)
    public EMaterialShadingModel ShadingModel;
    public Boolean bCastDynamicShadowAsMasked;
    public FMaterialShadingModelField ShadingModels;
    public Float OpacityMaskClipValue;
    public FVectorMaterialInput WorldPositionOffset;
    public FScalarMaterialInput Refraction;
    //public FMaterialAttributesInput MaterialAttributes;
    @UProperty(skipPrevious = 1)
    public FScalarMaterialInput PixelDepthOffset;
    //public FShadingModelMaterialInput ShadingModelFromMaterialExpression;
    @UProperty(skipPrevious = 1)
    public Boolean bEnableSeparateTranslucency;
    public Boolean bEnableResponsiveAA;
    public Boolean bScreenSpaceReflections;
    public Boolean bContactShadows;
    public Boolean TwoSided;
    public Boolean DitheredLODTransition;
    public Boolean DitherOpacityMask;
    public Boolean bAllowNegativeEmissiveColor;
    //public ETranslucencyLightingMode TranslucencyLightingMode;
    @UProperty(skipPrevious = 1)
    public Boolean bEnableMobileSeparateTranslucency;
    public Integer NumCustomizedUVs;
    public Float TranslucencyDirectionalLightingIntensity;
    public Float TranslucentShadowDensityScale;
    public Float TranslucentSelfShadowDensityScale;
    public Float TranslucentSelfShadowSecondDensityScale;
    public Float TranslucentSelfShadowSecondOpacity;
    public Float TranslucentBackscatteringExponent;
    public FLinearColor TranslucentMultipleScatteringExtinction;
    public Float TranslucentShadowStartOffset;
    public Boolean bDisableDepthTest;
    public Boolean bWriteOnlyAlpha;
    public Boolean bGenerateSphericalParticleNormals;
    public Boolean bTangentSpaceNormal;
    public Boolean bUseEmissiveForDynamicAreaLighting;
    public Boolean bBlockGI;
    public Boolean bUsedAsSpecialEngineMaterial;
    public Boolean bUsedWithSkeletalMesh;
    public Boolean bUsedWithEditorCompositing;
    public Boolean bUsedWithParticleSprites;
    public Boolean bUsedWithBeamTrails;
    public Boolean bUsedWithMeshParticles;
    public Boolean bUsedWithNiagaraSprites;
    public Boolean bUsedWithNiagaraRibbons;
    public Boolean bUsedWithNiagaraMeshParticles;
    public Boolean bUsedWithGeometryCache;
    public Boolean bUsedWithStaticLighting;
    public Boolean bUsedWithMorphTargets;
    public Boolean bUsedWithSplineMeshes;
    public Boolean bUsedWithInstancedStaticMeshes;
    public Boolean bUsedWithGeometryCollections;
    public Boolean bUsesDistortion;
    public Boolean bUsedWithClothing;
    public Boolean bUsedWithWater;
    public Boolean bUsedWithHairStrands;
    public Boolean bUsedWithLidarPointCloud;
    public Boolean bUsedWithVirtualHeightfieldMesh;
    public Boolean bUsedWithUI;
    public Boolean bAutomaticallySetUsageInEditor;
    public Boolean bFullyRough;
    public Boolean bUseFullPrecision;
    public Boolean bUseLightmapDirectionality;
    public Boolean bForwardRenderUsePreintegratedGFForSimpleIBL;
    public Boolean bUseHQForwardReflections;
    public Boolean bUsePlanarForwardReflections;
    public Boolean bNormalCurvatureToRoughness;
    //public EMaterialTessellationMode D3D11TessellationMode;
    @UProperty(skipPrevious = 1)
    public Boolean bEnableCrackFreeDisplacement;
    public Boolean bEnableAdaptiveTessellation;
    public Boolean AllowTranslucentCustomDepthWrites;
    public Boolean Wireframe;
    //public EMaterialShadingRate ShadingRate;
    @UProperty(skipPrevious = 1)
    public Boolean bCanMaskedBeAssumedOpaque;
    public Boolean bIsMasked;
    public Boolean bIsPreviewMaterial;
    public Boolean bIsFunctionPreviewMaterial;
    public Boolean bUseMaterialAttributes;
    public Boolean bCastRayTracedShadows;
    public Boolean bUseTranslucencyVertexFog;
    public Boolean bApplyCloudFogging;
    public Boolean bIsSky;
    public Boolean bComputeFogPerPixel;
    public Boolean bOutputTranslucentVelocity;
    public Boolean bAllowDevelopmentShaderCompile;
    public Boolean bIsMaterialEditorStatsMaterial;
    //public EBlendableLocation BlendableLocation;
    @UProperty(skipPrevious = 1)
    public Boolean BlendableOutputAlpha;
    public Boolean bEnableStencilTest;
    //public EMaterialStencilCompare StencilCompare;
    @UProperty(skipPrevious = 1)
    public UByte StencilRefValue;
    //public ERefractionMode RefractionMode;
    @UProperty(skipPrevious = 1)
    public Integer BlendablePriority;
    public Boolean bIsBlendable;
    public UInt UsageFlagWarnings;
    public Float RefractionDepthBias;
    public FGuid StateId;
    public Float MaxDisplacement;
    public FMaterialCachedExpressionData CachedExpressionData;

    public enum EMaterialDomain {
        MD_Surface,
        MD_DeferredDecal,
        MD_LightFunction,
        MD_Volume,
        MD_PostProcess,
        MD_UI,
        MD_RuntimeVirtualTexture
    }

    @UStruct
    public static class FMaterialShadingModelField {
        public UShort ShadingModelField;
    }

    @UStruct
    public static class FMaterialCachedExpressionData {
        public FMaterialCachedParameters Parameters;
        public List<FPackageIndex /*Object*/> ReferencedTextures;
        public List<FMaterialFunctionInfo> FunctionInfos;
        public List<FMaterialParameterCollectionInfo> ParameterCollectionInfos;
        public List<FPackageIndex /*MaterialFunctionInterface*/> DefaultLayers;
        public List<FPackageIndex /*MaterialFunctionInterface*/> DefaultLayerBlends;
        public List<FPackageIndex /*LandscapeGrassType*/> GrassTypes;
        public List<FName> DynamicParameterNames;
        public List<Boolean> QualityLevelsUsed;
        public Boolean bHasRuntimeVirtualTextureOutput;
        public Boolean bHasSceneColor;
    }

    @UStruct
    public static class FMaterialFunctionInfo {
        public FGuid StateId;
        public FPackageIndex /*MaterialFunctionInterface*/ Function;
    }

    @UStruct
    public static class FMaterialParameterCollectionInfo {
        public FGuid StateId;
        public FPackageIndex /*UMaterialParameterCollection*/ Function;
    }
}
