package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class AthenaDanceItemDefinition extends FortMontageItemDefinitionBase {
    public Boolean bMovingEmote;
    public Boolean bMovingEmoteSkipLandingFX;
    public Boolean bMoveForwardOnly;
    public Boolean bMoveFollowingOnly;
    public Boolean bGroupEmote;
    public Boolean bUseHighPreviewCamera;
    public Boolean bGroupAnimationSync;
    public Float WalkForwardSpeed;
    public FPackageIndex /*AthenaDanceItemDefinition*/ GroupEmoteToStartLeader;
    public FPackageIndex /*AthenaDanceItemDefinition*/ GroupEmoteToStartFollower;
    public FPackageIndex /*AthenaDanceItemDefinition*/ GroupEmoteToStartLeaderIfBothOwn;
    public FPackageIndex /*AthenaDanceItemDefinition*/ GroupEmoteToStartFollowerIfBothOwn;
    public List<FVariantSwapMontageData> MotageSelectionGroups;
    public FVector GroupEmotePositionOffset;
    public Float GroupEmotePositionOffsetTolerance;
    public Boolean bLockGroupEmoteLeaderRotation;
    public Float GroupEmoteLeaderRotationYawOffset;
    public Float GroupEmoteFollowerRotationYawOffset;
    public FSoftObjectPath FrontEndAnimation;
    public FSoftObjectPath FrontEndAnimationFemaleOverride;
    public FSoftObjectPath /*SoftClassPath*/ CustomDanceAbility;
    public FText ChatTriggerCommandName;

    @UStruct
    public static class FVariantSwapMontageData {
        public FGameplayTag VariantMetaTagRequired;
        public FName MontageSectionName;
    }
}
