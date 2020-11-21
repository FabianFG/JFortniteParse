package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.ue4.objects.FDataTableRowHandle;
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortCollectionBookSlotData extends FTableRowBase {
    public FName SlotXpWeightName;
    public FName SlotSourceId;
    public FName SlotSourceId2;
    public List<FSoftObjectPath> AllowedItems;
    public List<FGameplayTag> AllowedWorkerPersonalities;
    public FDataTableRowHandle PurchaseCosts;
}
