package me.fungames.jfortniteparse.ue4.assets.objects.mats;

import kotlin.ULong;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInstance;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

@UStruct
public class FMaterialCachedParameters {
    @UProperty(arrayDim = 5)
    public FMaterialCachedParameterEntry[] Entries;
    public List<Float> ScalarValues;
    public List<FLinearColor> VectorValues;
    public List<FPackageIndex /*Texture*/> TextureValues;
    public List<FPackageIndex /*Font*/> FontValues;
    public List<Integer> FontPageValues;
    public List<FPackageIndex /*RuntimeVirtualTexture*/> RuntimeVirtualTextureValues;

    @UStruct
    public static class FMaterialCachedParameterEntry {
        public List<ULong> NameHashes;
        public List<UMaterialInstance.FMaterialParameterInfo> ParameterInfos;
        public List<FGuid> ExpressionGuids;
        public List<Boolean> Overrides;
    }
}
