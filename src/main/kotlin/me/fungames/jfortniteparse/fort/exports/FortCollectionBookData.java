package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.FortItemQuantityPair;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class FortCollectionBookData extends UDataAsset {
    public FPackageIndex /*DataTable*/ PageCategoryData;
    public FPackageIndex /*DataTable*/ PageData;
    public FPackageIndex /*DataTable*/ SectionData;
    public FPackageIndex /*DataTable*/ SlotData;
    public FPackageIndex /*DataTable*/ SlotSourceData;
    public FPackageIndex /*DataTable*/ XPWeightData;
    public FPackageIndex /*CurveFloat*/ SlotRarityFactorData;
    public FPackageIndex /*DataTable*/ BookXPData;
    public List<FortItemQuantityPair> UnslotCost;
    public FGameplayTagContainer UnslotCatalysts;
}
