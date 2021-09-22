package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.FortAbilitySetDeliveryInfo;
import me.fungames.jfortniteparse.fort.objects.FortDeliveryInfoRequirementsFilter;
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
}
