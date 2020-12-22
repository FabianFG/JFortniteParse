package me.fungames.jfortniteparse.fort.exports;

import kotlin.Lazy;
import me.fungames.jfortniteparse.fort.enums.EFortResourceType;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface;
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D;

public class BuildingTextureData extends UDataAsset {
    public Lazy<UTexture2D> Diffuse;
    public Lazy<UTexture2D> Normal;
    public Lazy<UTexture2D> Specular;
    public Lazy<UMaterialInterface> OverrideMaterial;
    public EFortTextureDataType Type;
    public EFortResourceType ResourceType;
    @UProperty(arrayDim = 5)
    public Float[] ResourceCost;

    public enum EFortTextureDataType {
        Any,
        OuterWall,
        InnerWall,
        Corner,
        Floor,
        Ceiling,
        Trim,
        Roof,
        Pillar,
        Shingle,
        None
    }
}
