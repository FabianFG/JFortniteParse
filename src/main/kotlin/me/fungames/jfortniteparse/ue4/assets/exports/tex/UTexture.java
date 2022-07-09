package me.fungames.jfortniteparse.ue4.assets.exports.tex;

import me.fungames.jfortniteparse.ue4.assets.exports.UStreamableRenderAsset;
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UUnrealMaterial;
import me.fungames.jfortniteparse.ue4.converters.CMaterialParams;
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid;
import me.fungames.jfortniteparse.ue4.objects.engine.FPerPlatformFloat;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UTexture extends UStreamableRenderAsset implements UUnrealMaterial {
    public FGuid LightingGuid;
    public Integer LodBias;
    public TextureCompressionSettings CompressionSettings;
    public TextureFilter Filter;
    public ETextureMipLoadOptions MipLoadOptions;
    public TextureGroup LODGroup;
    public FPerPlatformFloat Downscale;
    public ETextureDownscaleOptions DownscaleOptions;
    public Boolean SRGB;
    public Boolean bNoTiling;
    public Boolean VirtualTextureStreaming;
    public Boolean CompressionYCoCg;
    public Boolean bNotOfflineProcessed;
    public Boolean bAsyncResourceReleaseHasBeenStarted;
    public List<FPackageIndex> AssetUserData;

    @Override
    public void getParams(CMaterialParams params) {
        //???
    }

    @NotNull
    @Override
    public String name() {
        return getName();
    }

    public enum TextureCompressionSettings {
        TC_Default,
        TC_Normalmap,
        TC_Masks,
        TC_Grayscale,
        TC_Displacementmap,
        TC_VectorDisplacementmap,
        TC_HDR,
        TC_EditorIcon,
        TC_Alpha,
        TC_DistanceFieldFont,
        TC_HDR_Compressed,
        TC_BC7,
        TC_HalfFloat
    }

    public enum TextureFilter {
        TF_Nearest,
        TF_Bilinear,
        TF_Trilinear,
        TF_Default
    }

    public enum ETextureMipLoadOptions {
        Default,
        AllMips,
        OnlyFirstMip
    }

    public enum TextureGroup {
        TEXTUREGROUP_World,
        TEXTUREGROUP_WorldNormalMap,
        TEXTUREGROUP_WorldSpecular,
        TEXTUREGROUP_Character,
        TEXTUREGROUP_CharacterNormalMap,
        TEXTUREGROUP_CharacterSpecular,
        TEXTUREGROUP_Weapon,
        TEXTUREGROUP_WeaponNormalMap,
        TEXTUREGROUP_WeaponSpecular,
        TEXTUREGROUP_Vehicle,
        TEXTUREGROUP_VehicleNormalMap,
        TEXTUREGROUP_VehicleSpecular,
        TEXTUREGROUP_Cinematic,
        TEXTUREGROUP_Effects,
        TEXTUREGROUP_EffectsNotFiltered,
        TEXTUREGROUP_Skybox,
        TEXTUREGROUP_UI,
        TEXTUREGROUP_Lightmap,
        TEXTUREGROUP_RenderTarget,
        TEXTUREGROUP_MobileFlattened,
        TEXTUREGROUP_ProcBuilding_Face,
        TEXTUREGROUP_ProcBuilding_LightMap,
        TEXTUREGROUP_Shadowmap,
        TEXTUREGROUP_ColorLookupTable,
        TEXTUREGROUP_Terrain_Heightmap,
        TEXTUREGROUP_Terrain_Weightmap,
        TEXTUREGROUP_Bokeh,
        TEXTUREGROUP_IESLightProfile,
        TEXTUREGROUP_Pixels2D,
        TEXTUREGROUP_HierarchicalLOD,
        TEXTUREGROUP_Impostor,
        TEXTUREGROUP_ImpostorNormalDepth,
        TEXTUREGROUP_8BitData,
        TEXTUREGROUP_16BitData,
        TEXTUREGROUP_Project01,
        TEXTUREGROUP_Project02,
        TEXTUREGROUP_Project03,
        TEXTUREGROUP_Project04,
        TEXTUREGROUP_Project05,
        TEXTUREGROUP_Project06,
        TEXTUREGROUP_Project07,
        TEXTUREGROUP_Project08,
        TEXTUREGROUP_Project09,
        TEXTUREGROUP_Project10,
        TEXTUREGROUP_Project11,
        TEXTUREGROUP_Project12,
        TEXTUREGROUP_Project13,
        TEXTUREGROUP_Project14,
        TEXTUREGROUP_Project15
    }

    public enum ETextureDownscaleOptions {
        Default,
        Unfiltered,
        SimpleAverage,
        Sharpen0,
        Sharpen1,
        Sharpen2,
        Sharpen3,
        Sharpen4,
        Sharpen5,
        Sharpen6,
        Sharpen7,
        Sharpen8,
        Sharpen9,
        Sharpen10
    }
}
