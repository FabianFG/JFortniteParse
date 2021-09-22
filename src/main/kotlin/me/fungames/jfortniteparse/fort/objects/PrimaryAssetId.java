package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;

import java.util.Locale;

@UStruct
public class PrimaryAssetId {
    public PrimaryAssetType PrimaryAssetType;
    public FName PrimaryAssetName;

    @UStruct
    public static class PrimaryAssetType {
        public FName Name;
    }

    @Override
    public String toString() {
        return PrimaryAssetType.Name.toString() + ':' + PrimaryAssetName.toString().toLowerCase(Locale.ROOT);
    }
}
