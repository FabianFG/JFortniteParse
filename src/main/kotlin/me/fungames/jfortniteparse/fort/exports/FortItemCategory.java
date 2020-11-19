package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.ItemCategory;
import me.fungames.jfortniteparse.fort.objects.ItemCategoryMappingData;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;

import java.util.List;

public class FortItemCategory extends UDataAsset {
    public List<ItemCategoryMappingData> PrimaryCategories;
    public List<ItemCategory> SecondaryCategories;
    public List<ItemCategory> TertiaryCategories;
}
