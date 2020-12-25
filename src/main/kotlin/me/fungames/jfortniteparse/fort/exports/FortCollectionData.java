package me.fungames.jfortniteparse.fort.exports;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;

import java.util.List;

public class FortCollectionData extends UDataAsset {
    public List<Lazy<FortCollectionDataEntry>> Entries;
}
