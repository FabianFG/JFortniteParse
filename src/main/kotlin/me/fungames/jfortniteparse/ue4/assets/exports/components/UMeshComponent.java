package me.fungames.jfortniteparse.ue4.assets.exports.components;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface;

import java.util.List;

public class UMeshComponent extends UPrimitiveComponent {
    public List<Lazy<UMaterialInterface>> OverrideMaterials;
    public Boolean bEnableMaterialParameterCaching;
}
