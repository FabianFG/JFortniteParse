package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortConversionControlKeyCosts extends FTableRowBase {
    public FSoftObjectPath RequiredItem;
    public FortConversionTierData Common;
    public FortConversionTierData Uncommon;
    public FortConversionTierData Rare;
    public FortConversionTierData Epic;
    public FortConversionTierData Legendary;
    public FortConversionTierData Mythic;
    public FortConversionTierData Transcendent;
    public FortConversionTierData Unattainable;
    public FGameplayTagContainer RequiredCatalysts;

    @UStruct
    public static class FortConversionTierData {
        public Integer TierCost;
        public Integer RequiredItemQuantity;
    }
}
