package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.AthenaRewardItemReference;
import me.fungames.jfortniteparse.fort.objects.ChallengeGiftBoxData;
import me.fungames.jfortniteparse.fort.objects.FortChallengeSetStyle;
import me.fungames.jfortniteparse.fort.objects.FortItemQuantityPair;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortChallengeBundleItemDefinition extends FortAccountItemDefinition {
    public BundleVisibilityData BundleVisiblityData;
    public List<FortChallengeBundleQuestEntry> QuestInfos;
    public List<FSoftObjectPath> SuppressedQuestDefs;
    public String CalendarEventTag;
    public String CalendarEventName;
    public List<FortChallengeBundleRewards> BundleCompletionRewards;
    public List<FortChallengeBundleLevel> BundleLevelRewards;
    public List<FortChallengeBundleSpecialOffer> SpecialOffers;
    public List<FSoftObjectPath> CareerQuestBitShifts;
    public BundleGoalCardDisplayData GoalCardDisplayData;
    public FGameplayTag GoalCardPinSeasonItemTag;
    public FSoftObjectPath NotificationIconOverride;
    public FortChallengeSetStyle DisplayStyle;
    public AthenaRewardItemReference OverrideRewardItem;
    public FPackageIndex /*AthenaCharacterItemDefinition*/ CharacterOverrideForRewardPreviews;
    public Integer MaxChainDepth;
    public FSoftObjectPath BundleHidenImageMaterial;
    public FText UniqueLockedMessage;
    public FText LockedDisplayTextOverride;
    public Boolean bHideFromMapChallenges;
    public Boolean bHideFromMapChallengeUntilBundleExists;
    public Boolean bHideWhenCompleted;
    public FSoftObjectPath HideUntilBundleCompleted;
    public Boolean bHideRewardFromMapChallenges;
    public Boolean bHideTimeRemaining;
    public Boolean bSkipAddToGoalBundles;
    public Boolean bDeleteProgressTokenOnRemoval;
    public Boolean bGetActiveQuestInChain;

    @UStruct
    public static class BundleVisibilityData {
        public Boolean bIsVisibleWhenClaimed;
        public Boolean bIsVisibleWhenCompleted;
        public Boolean bIsVisibleWhenExpired;
        public Boolean bForceFinalQuestToBeVisible;
    }

    @UStruct
    public static class FortChallengeBundleQuestEntry {
        public FSoftObjectPath QuestDefinition;
        public EChallengeBundleQuestUnlockType QuestUnlockType;
        public Boolean bStartActive;
        public Boolean bIsPrerequisite;
        public Integer UnlockValue;
        public ChallengeGiftBoxData RewardGiftBox;
        public FortItemQuantityPair MenuOverrideRewardPreview;
    }

    public enum EChallengeBundleQuestUnlockType {
        Manually,
        GrantWithBundle,
        RequiresBattlePass,
        DaysFromEventStart,
        ChallengesCompletedToUnlock,
        BundleLevelup
    }

    @UStruct
    public static class FortChallengeBundleRewards {
        public Integer CompletionCount;
        public Boolean bBundlePrestige;
        public List<AthenaRewardItemReference> Rewards;
    }

    @UStruct
    public static class FortChallengeBundleLevel {
        public List<FortChallengeBundleLevelReward> BundleLevelRewardEntries;
    }

    @UStruct
    public static class FortChallengeBundleLevelReward {
        public AthenaRewardItemReference RewardItem;
        public Integer NumObjectivesNeeded;
    }

    @UStruct
    public static class FortChallengeBundleSpecialOffer {
        public String Storefront;
        public FText RichText;
        public FSoftObjectPath OfferImage;
    }

    @UStruct
    public static class BundleGoalCardDisplayData {
        public FText HeaderText;
        public FLinearColor HeaderColor;
        public FText SubHeaderText;
        public FLinearColor SubHeaderColor;
        public FSoftObjectPath HeaderIcon;
        public Integer MilestoneTier;
        public Integer SortOrder;
    }
}
