package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.GameplayEffectApplicationInfo;
import me.fungames.jfortniteparse.fort.objects.GameplayEffectApplicationInfoHard;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UPrimaryDataAsset;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagQuery;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class FortAbilitySet extends UPrimaryDataAsset {
    public List<FPackageIndex /*FortGameplayAbility*/> GameplayAbilities;
    public List<GameplayEffectApplicationInfoHard> GrantedGameplayEffects;
    public FGameplayTag AbilityActivatedByInputKeyTag;
    public FGameplayTag AbilityActivatedByInputTag;
    public FGameplayTag AbilityActivatedByInputCooldownTag;
    public FGameplayTag AbilityActivatedByInputExitBuildModeTag;
    public List<AbilityActivatedByInputData> AbilitiesActivatedByInput;
    public List<FPackageIndex /*FortItemDefinition*/> AdditionalItemsToAdd;
    public List<GameplayEffectApplicationInfo> PassiveGameplayEffects;
    public FGameplayTagContainer AnalyticsTags;

    @UStruct
    public static class AbilityActivatedByInputData {
        public FPackageIndex /*FortGameplayAbility*/ Ability;
        public FGameplayTagQuery ActivationTagQuery;
    }
}
