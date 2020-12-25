package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.exports.UPrimaryDataAsset;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortTandemCharacterData extends UPrimaryDataAsset {
    public FGameplayTag GameplayTag;
    public FGameplayTagContainer POILocations;
    public List<FText> POITextOverrides;
    public FText DisplayName;
    public FText GeneralDescription;
    public FText AdditionalDescription;
    public FText BehaviorDescription;
    public FSoftObjectPath ToastIcon;
    public FSoftObjectPath EntryListIcon;
    public FSoftObjectPath SidePanelIcon;
}
