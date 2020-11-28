package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.FortItemQuantityPair;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortAlterationItemDefinition extends FortAccountItemDefinition {
    public EFortAlteration AlterationType;
    public FortCosmeticModification DefaultCosmetic;
    public List<FortConditionalCosmeticModification> ConditionalCosmetics;
    public List<FortConditionalIncludeTags> AdditionalGameplayTags;
    public FSoftObjectPath AlterationAbilitySet;
    public List<FortItemQuantityPair> AdditionalRespecCosts;

    public enum EFortAlteration {
        AttributeSlot,
        GameplaySlot,
        ComplexCosmeticSlot,
        UserPickedCosmeticSlot,
        ColorSlot,
        HeroSpecializationTier1Slot,
        HeroSpecializationTier2Slot,
        HeroSpecializationTier3Slot,
        HeroSpecializationTier4Slot,
        HeroSpecializationTier5Slot,
        EFortAlteration_MAX
    }

    @UStruct
    public static class FortCosmeticModification {
        public FSoftObjectPath CosmeticMaterial;
        public FSoftObjectPath AmbientParticleSystem;
        public FSoftObjectPath MuzzleParticleSystem;
        public FSoftObjectPath MuzzleNiagaraSystem;
        public FSoftObjectPath ReloadParticleSystem;
        public FSoftObjectPath BeamParticleSystem;
        public FSoftObjectPath BeamNiagaraSystem;
        @UProperty(arrayDim = 27)
        public FSoftObjectPath[] ImpactPhysicalSurfaceEffects;
        public List<FSoftObjectPath> ImpactNiagaraPhysicalSurfaceEffects;
        public FSoftObjectPath /*SoftClassPath*/ TracerTemplate;
        public Boolean bModifyColor;
        public FLinearColor ColorAlteration;
        public FName ColorParameterName;
        public Boolean bModifyDecalColour;
        public FLinearColor DecalColourAlterationStart;
        public FLinearColor DecalColourAlterationEnd;
        public Boolean bModifyShellColour;
        public FLinearColor ShellColourAlteration;
    }

    @UStruct
    public static class FortConditionalCosmeticModification {
        public FortCosmeticModification CosmeticModification;
        public FGameplayTagContainer ConditionalTags;
    }

    @UStruct
    public static class FortConditionalIncludeTags {
        public FGameplayTagContainer ConditionTags;
        public FGameplayTagContainer IncludeTags;
    }
}
