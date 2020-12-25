package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.FScalableFloat;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortCollectionDataEntryFish extends FortCollectionDataEntry {
    public FGameplayTag EntryTag;
    public FText EntryName;
    public FText EntryDescription;
    public FText AdditionalEntryDescription;
    public FSoftObjectPath SmallIcon;
    public FSoftObjectPath LargeIcon;
    public FScalableFloat Size;
    public Boolean bNeedsProFishingRod;
}
