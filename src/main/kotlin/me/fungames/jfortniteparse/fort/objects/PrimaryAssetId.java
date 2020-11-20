package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;

@UStruct
public class PrimaryAssetId {
    public PrimaryAssetType PrimaryAssetType;
    public FName PrimaryAssetName;

    @UStruct
    public static class PrimaryAssetType {
        public FName Name;
    }
}
