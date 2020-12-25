package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.fort.objects.FortItemQuantityPair;
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;

import java.util.List;

public class FortCollectionBookDirectPurchaseData extends FTableRowBase {
    public List<FortItemQuantityPair> PurchaseCosts;
    public FGameplayTagContainer PurchaseCatalysts;
}
