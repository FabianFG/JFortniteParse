package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;

import java.util.Locale;

@UStruct
public class FortItemQuantityPair {
    public PrimaryAssetId ItemPrimaryAssetId;
    public Integer Quantity;

    @Override
    public String toString() {
        return ItemPrimaryAssetId.PrimaryAssetType.Name.toString() + ':' + ItemPrimaryAssetId.PrimaryAssetName.toString().toLowerCase(Locale.ENGLISH);
    }
}
