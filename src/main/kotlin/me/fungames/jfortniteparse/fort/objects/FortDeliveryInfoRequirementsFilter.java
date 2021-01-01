package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.fort.enums.EFortTeam;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;

@UStruct
public class FortDeliveryInfoRequirementsFilter {
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

    public enum EFortDeliveryInfoBuildingActorSpecification {
        All,
        PlayerBuildable,
        NonPlayerBuildable
    }
}
