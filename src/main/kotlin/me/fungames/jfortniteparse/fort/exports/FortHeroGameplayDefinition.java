package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.enums.EFortItemTier;
import me.fungames.jfortniteparse.fort.enums.EFortRarity;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagQuery;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortHeroGameplayDefinition extends UDataAsset {
    public FPackageIndex /*FortHeroClassGameplayDefinition*/ HeroClassGameplayDefinition;
    public FGameplayTagContainer HeroBaseStatlineTags;
    public FGameplayTagContainer HeroTags;
    public List<FortHeroTierAbilityKit> TierAbilityKits;
    public FortHeroGameplayPiece HeroPerk;
    public FortHeroGameplayPiece CommanderPerk;

    @UStruct
    public static class FortHeroTierAbilityKit {
        public FSoftObjectPath GrantedAbilityKit;
        public EFortRarity MinimumHeroRarity;
    }

    @UStruct
    public static class FortHeroGameplayPiece {
        public FSoftObjectPath GrantedAbilityKit;
        public FGameplayTagQuery RequiredCommanderTagQuery;
        public FText CommanderRequirementsText;
        public Boolean bUseGlobalDefaultMinima;
        public EFortItemTier MinimumHeroTier;
        public Integer MinimumHeroLevel;
        public EFortRarity MinimumHeroRarity;
    }
}
