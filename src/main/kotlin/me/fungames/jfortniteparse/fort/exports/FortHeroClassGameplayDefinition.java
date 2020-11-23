package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;
import me.fungames.jfortniteparse.ue4.objects.FDataTableRowHandle;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortHeroClassGameplayDefinition extends UDataAsset {
    public FSoftObjectPath /*SoftClassPath*/ OverridePawnClass;
    public FGameplayTagContainer HeroClassTags;
    public FDataTableRowHandle LegacyStatHandle;
    public List<FSoftObjectPath> ClassAbilityKits;
}
