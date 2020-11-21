package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.AthenaRewardItemReference;
import me.fungames.jfortniteparse.fort.objects.ChallengeGiftBoxData;
import me.fungames.jfortniteparse.fort.objects.FortChallengeSetStyle;
import me.fungames.jfortniteparse.fort.objects.FortItemQuantityPair;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortChallengeBundleItemDefinition extends FortAccountItemDefinition {
    public List<FortChallengeBundleQuestEntry> QuestInfos;
    public List<FSoftObjectPath> SuppressedQuestDefs;
    public String CalendarEventTag;
    public String CalendarEventName;
    public List<FortChallengeBundleRewards> BundleCompletionRewards;
    public List<FortChallengeBundleLevel> BundleLevelRewards;
    public List<FortChallengeBundleSpecialOffer> SpecialOffers;
    public List<FSoftObjectPath> CareerQuestBitShifts;
    public FortChallengeSetStyle DisplayStyle;
    public AthenaRewardItemReference OverrideRewardItem;
    public FPackageIndex /*AthenaCharacterItemDefinition*/ CharacterOverrideForRewardPreviews;
    public Integer MaxChainDepth;
    public FSoftObjectPath BundleHidenImageMaterial;
    public FText UniqueLockedMessage;
    public Boolean bHideFromMapChallenges;
    public Boolean bHideRewardFromMapChallenges;

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
}
