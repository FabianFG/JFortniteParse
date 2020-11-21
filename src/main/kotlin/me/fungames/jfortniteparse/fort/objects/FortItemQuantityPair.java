package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;

@UStruct
public class FortItemQuantityPair {
    public PrimaryAssetId ItemPrimaryAssetId;
    public Integer Quantity;

    @Override
    public String toString() {
        return String.format("%d x %s", Quantity, ItemPrimaryAssetId.toString());
    }
}
