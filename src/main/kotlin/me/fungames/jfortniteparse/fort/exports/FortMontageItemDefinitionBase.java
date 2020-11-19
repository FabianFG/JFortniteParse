package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.enums.EFortCustomBodyType;
import me.fungames.jfortniteparse.fort.enums.EFortCustomGender;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortMontageItemDefinitionBase extends AthenaCosmeticItemDefinition {
    public FSoftObjectPath Animation;
    public FSoftObjectPath AnimationFemaleOverride;
    public List<FFortEmoteMapping> AnimationOverrides;
    public Integer PreviewLoops;
    public Float PreviewLength;
    public Float EmoteCooldownSecs;
    public Boolean bMontageContainsFacialAnimation;
    public Boolean bPlayRandomSection;
    public List<FSectionNameAndWeight> PlayRandomSectionByName;
    public Boolean bSwitchToHarvestingToolOnUse;
    public Boolean bHolsterWeapon;
    public Boolean bHolsterWeaponIfDualWieldPickaxe;
    public List<FMontageItemAccessData> AccessData;
    public List<FMontageVisibilityData> VisibilityData;
    public FGameplayTagContainer EmoteWheelExclusiveTags;
    public FSoftObjectPath RequiredEmoteParent;
    public FGameplayTagContainer TagsWhichIndicateEmoteParent;

    @UStruct
    public static class FFortEmoteMapping {
        public EFortCustomBodyType BodyType;
        public EFortCustomGender Gender;
        public FSoftObjectPath EmoteMontage;
    }

    @UStruct
    public static class FSectionNameAndWeight {
        public FName SectionName;
        public Float SectionWeight;
    }

    @UStruct
    public static class FMontageItemAccessData {
        public FGameplayTag AccessTag;
        public FPackageIndex /*FortItemAccessTokenType*/ AccessToken;
    }

    @UStruct
    public static class FMontageVisibilityData {
        public EMontageVisibilityRule Rule;
        public FPackageIndex /*FortItemDefinition*/ Item;
    }

    public enum EMontageVisibilityRule {
        RequiredItem,
        ForbiddenItem
    }
}
