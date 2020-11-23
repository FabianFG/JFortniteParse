package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.FortAttributeInitializationKey;
import me.fungames.jfortniteparse.ue4.objects.FDataTableRowHandle;
import me.fungames.jfortniteparse.ue4.objects.FGameplayAttribute;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortGadgetItemDefinition extends FortWorldItemDefinition {
    public Integer GadgetPriority;
    public Boolean bDestroyGadgetWhenTrackedAttributesIsZero;
    public Boolean bHasChargeUp;
    public Boolean bDropAllOnEquip;
    public Boolean bCanChangePreviewImageDuringGame;
    public List<FPackageIndex /*CustomCharacterPart*/> CharacterParts;
    public FGameplayTagContainer SkinMetaTagsToSkip;
    public FGameplayTag PartSwapTag;
    public List<CharacterPartsExtraSpecial> CharacterPartsExtraSpecial;
    public FSoftObjectPath /*SoftClassPath*/ AnimBPOverride;
    public FSoftObjectPath FootstepBankOverride;
    public FSoftObjectPath AbilitySet;
    public FSoftObjectPath /*SoftClassPath*/ AttributeSet;
    public FortAttributeInitializationKey AttributeInitKey;
    public List<FGameplayAttribute> TrackedAttributes;
    public FSoftObjectPath OnDestroyParticleSystem;
    public FSoftObjectPath OnDestroySound;
    public FName OnDestroyAttachToSocketName;
    public FSoftObjectPath /*SoftClassPath*/ GameplayAbility;
    public FGameplayTagContainer HUDVisibilityTags;
    public FSoftObjectPath WeaponItemDefinition;
    public List<FSoftObjectPath> AdditionalItemsToLoadWhenEquipped;
    public Boolean bValidForLastEquipped;
    public FGameplayAttribute LevelAttribute;
    public FDataTableRowHandle DamageStatHandle;
    public String NodeId;

    public static class CharacterPartsExtraSpecial {
        public List<FPackageIndex /*CustomCharacterPart*/> CharacterPartsForExtraSpecial;
        public FGameplayTagContainer SkinMetaTagsForExtraSpecial;
    }
}
