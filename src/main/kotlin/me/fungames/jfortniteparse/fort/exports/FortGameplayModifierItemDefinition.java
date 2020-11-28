package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.GameplayEffectApplicationInfo;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortGameplayModifierItemDefinition extends FortAccountItemDefinition {
    public List<FortGameplayEffectDeliveryInfo> PersistentGameplayEffects;
    public List<FortAbilitySetDeliveryInfo> PersistentAbilitySets;
    public List<FSoftObjectPath /*SoftClassPath*/> Mutators;
    public Boolean bHiddenInUI;
    public FGameplayTagContainer DisallowedPlaylistNameTags;
    public FGameplayTagContainer DynamicPlaylistNameTags;

    @UStruct
    public static class FortGameplayEffectDeliveryInfo {
        public FortDeliveryInfoRequirementsFilter DeliveryRequirements;
        public List<GameplayEffectApplicationInfo> GameplayEffects;
    }

    @UStruct
    public static class FortDeliveryInfoRequirementsFilter {
        public FGameplayTagRequirements SourceTagRequirements;
        public FGameplayTagRequirements TargetTagRequirements;
        public EFortTeamAffiliation ApplicableTeamAffiliation;
        public Boolean bConsiderTeamAffiliationToInstigator;
        public EFortTeam ApplicableTeam;
        public Boolean bConsiderTeam;
        public Boolean bApplyToPlayerPawns;
        public Boolean bApplyToAIPawns;
        public Boolean bApplyToBuildingActors;
        public EFortDeliveryInfoBuildingActorSpecification BuildingActorSpecification;
        public Boolean bApplyToGlobalEnvironmentAbilityActor;
    }

    @UStruct
    public static class FGameplayTagRequirements { // TODO move to GameplayAbilities
        public FGameplayTagContainer RequireTags;
        public FGameplayTagContainer IgnoreTags;
    }

    public enum EFortTeamAffiliation {
        Friendly,
        Neutral,
        Hostile
    }

    public enum EFortTeam {
        Spectator,
        HumanCampaign,
        Monster,
        HumanPvP_Team1,
        HumanPvP_Team2
    }

    public enum EFortDeliveryInfoBuildingActorSpecification {
        All,
        PlayerBuildable,
        NonPlayerBuildable
    }

    @UStruct
    public static class FortAbilitySetDeliveryInfo {
        public FortDeliveryInfoRequirementsFilter DeliveryRequirements;
        public List<FSoftObjectPath> AbilitySets;
    }
}
