package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.enums.EFortItemTier;
import me.fungames.jfortniteparse.fort.enums.EFortRarity;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagQuery;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortTeamPerkItemDefinition extends FortProfileItemDefinition {
    public FSoftObjectPath GrantedAbilityKit;
    public Boolean bProgressiveBonus;
    public FGameplayTagQuery RequiredCommanderTagQuery;
    public FText CommanderRequirementsText;
    public List<FortTeamPerkLoadoutCondition> TeamPerkLoadoutConditions;

    @UStruct
    public static class FortTeamPerkLoadoutCondition {
        public Integer NumTimesSatisfiable;
        public FGameplayTagQuery RequiredTagQuery;
        public Boolean bConsiderMinimumTier;
        public Boolean bConsiderMaximumTier;
        public Boolean bConsiderMinimumLevel;
        public Boolean bConsiderMaximumLevel;
        public Boolean bConsiderMinimumRarity;
        public Boolean bConsiderMaximumRarity;
        public EFortItemTier MinimumHeroTier;
        public EFortItemTier MaximumHeroTier;
        public Integer MinimumHeroLevel;
        public Integer MaximumHeroLevel;
        public EFortRarity MinimumHeroRarity;
        public EFortRarity MaximumHeroRarity;
    }
}
