package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.enums.EItemProfileType;
import me.fungames.jfortniteparse.fort.objects.CosmeticVariantInfo;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class FortVariantTokenType extends FortAccountItemDefinition {
    public EItemProfileType ProfileType;
    public FPackageIndex /*FortItemDefinition*/ cosmetic_item;
    public FGameplayTag VariantChanelTag;
    public FGameplayTag VariantNameTag;
    public List<CosmeticVariantInfo> VariantPreviewOverrides;
    public Boolean bAutoEquipVariant;
    public Boolean bMarkItemUnseen;
    public Boolean bCreateGiftbox;
    public String CustomGiftbox;
}
