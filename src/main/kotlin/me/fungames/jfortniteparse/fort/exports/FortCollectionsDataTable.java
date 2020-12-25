package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortCollectionsDataTable extends UDataAsset {
    public List<FortCollectionDataMapping> Collections;

    @UStruct
    public static class FortCollectionDataMapping {
        public String CollectionType;
        public FSoftObjectPath Collection;
    }
}
