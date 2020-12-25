package me.fungames.jfortniteparse.fort.exports;

import kotlin.Lazy;
import me.fungames.jfortniteparse.fort.objects.FortItemQuantityPair;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable;
import me.fungames.jfortniteparse.ue4.objects.engine.curves.UCurveFloat;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;

import java.util.List;

public class FortCollectionBookData extends UDataAsset {
    public Lazy<UDataTable> PageCategoryData;
    public Lazy<UDataTable> PageData;
    public Lazy<UDataTable> SectionData;
    public Lazy<UDataTable> SlotData;
    public Lazy<UDataTable> SlotSourceData;
    public Lazy<UDataTable> XPWeightData;
    public Lazy<UCurveFloat> SlotRarityFactorData;
    public Lazy<UDataTable> BookXPData;
    public List<FortItemQuantityPair> UnslotCost;
    public FGameplayTagContainer UnslotCatalysts;
}
