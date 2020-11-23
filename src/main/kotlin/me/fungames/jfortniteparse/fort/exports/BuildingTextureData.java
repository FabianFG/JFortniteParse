package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

public class BuildingTextureData extends UDataAsset {
    public FPackageIndex /*Texture2D*/ Diffuse;
    public FPackageIndex /*Texture2D*/ Normal;
    public FPackageIndex /*Texture2D*/ Specular;
    public FPackageIndex /*MaterialInterface*/ OverrideMaterial;
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

    public enum EFortResourceType {
        Wood,
        Stone,
        Metal,
        Permanite,
        GoldCurrency,
        None
    }
}
