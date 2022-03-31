package me.fungames.jfortniteparse.fort.objects.variants;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

@UStruct
public class ItemTextureVariantDef extends BaseVariantDef {
    public ItemTextureVariant InnerDef;
    public CosmeticMetaTagContainer MetaTags;
    public FGameplayTagContainer FilterOutItemsWithTags;
    public Boolean bWantsSprays;
    public Boolean bWantsEmoji;
    public boolean bAllowClear = true;

    @UStruct
    public static class ItemTextureVariant {
        public List<FSoftObjectPath> MaterialsToAlter;
        public FName ParamName;
        public String DefaultSelectedItem;
    }
}
