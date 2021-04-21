package me.fungames.jfortniteparse.fort.exports;

import kotlin.Lazy;
import me.fungames.jfortniteparse.fort.objects.AthenaRewardItemReference;
import me.fungames.jfortniteparse.fort.objects.PrimaryAssetId;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class AthenaSeasonItemDefinition extends FortAccountItemDefinition {
    public Boolean bXpOnlySeason;
    public Boolean bUseAccoladePunchCard;
    public Lazy<UDataTable> SeasonXpOnlyExtendedCurve;
    public Lazy<FortMedalsPunchCardItemDefinition> DailyPunchCard;
    public Lazy<FortRepeatableDailiesCardItemDefinition> RepeatableDailiesCard;
    public Integer RestedXpDailyGrant;
    public Integer RestedXpMaxAccrue;
    public Float RestedXpMultiplier;
    public Integer SeasonStartCalendarOffsetDays;
    public Integer SeasonNumber;
    public Integer NumSeasonLevels;
    public Integer NumBookLevels;
    public Integer NumAdditionalBookLevels;
    public EAthenaSeasonShopVisibility SeasonShopVisibility;
    public EAthenaChallengeTabVisibility ChallengesVisibility;
    public Lazy<UDataTable> SeasonXpCurve;
    public Lazy<UDataTable> BookXpCurve;
    public String SeasonStorefront;
    @Deprecated
    public String BattlePassOfferId;
    public List<String> BattlePassOfferIds;
    @Deprecated
    public String BattlePassLevelOfferID;
    public List<String> BattlePassLevelOfferIDs;
    public String BattlePassLevelBundleOfferID;
    public List<PrimaryAssetId> FreeTokenItemPrimaryAssetIds;
    public List<Integer> FreeLevelsThatNavigateToBattlePass;
    public List<Integer> FreeLevelsThatAutoOpenTheAboutScreen;
    public List<TrackCategory> TrackCategories;
    public List<TrackDynamicBackground> TrackPageBackgrounds;
    public AthenaRewardSchedule SeasonXpScheduleFree;
    public FGameplayTag FreeSeasonItemContentTag;
    public AthenaRewardSchedule BookXpScheduleFree;
    public FGameplayTag BattlePassFreeItemContentTag;
    public AthenaRewardSchedule BookXpSchedulePaid;
    public FGameplayTag BattlePassPaidItemContentTag;
    public AthenaRewardSchedule AdditionalBookSchedule;
    public FGameplayTag BattlePassAdditionalItemContentTag;
    public AthenaSeasonBannerLevelSchedule SeasonBannerSchedule;
    public Lazy<FortChallengeBundleItemDefinition> SeasonalGlyphChallengeBundle;
    public String GlyphTokenTemplateId;
    public Lazy<UDataTable> SeasonalGlyphRewards;
    public Lazy<FortChallengeBundleScheduleDefinition> ChallengeSchedulePaid;
    public List<Lazy<FortChallengeBundleScheduleDefinition>> ChallengeSchedulesAlwaysShown;
    public AthenaRewardScheduleLevel SeasonGrantsToEveryone;
    public FGameplayTag SeasonGrantsToEveryoneItemContentTag;
    public AthenaRewardScheduleLevel SeasonFirstWinRewards;
    public FGameplayTag SeasonFirstWinItemContentTag;
    public AthenaRewardScheduleLevel BattleStarSubstitutionReward;
    public List<XpDisplayConversion> XpDisplayOverride;
    public FSoftObjectPath XpItemDef;
    public List<FSoftObjectPath> ExpiringRewardTypes;
    public List<FSoftObjectPath> TokensToRemoveAtSeasonEnd;
    public List<AthenaMidSeasonUpdate> MidSeasonUpdates;
    public Boolean bRemoveAllDailyQuestsAtSeasonEnd;
    public FSoftObjectPath NoBattleBundleToken;
    public FSoftObjectPath CollectionsDataTable;
    public List<FGameplayTag> FirstTimeTrackedBitFlags;

    public enum EAthenaSeasonShopVisibility {
        Hide,
        ShowIfOffersAvailable,
        ShowAlways
    }

    public enum EAthenaChallengeTabVisibility {
        Hide,
        ShowAlways
    }

    @UStruct
    public static class TrackCategory {
        public FSoftObjectPath CategoryIcon;
        public FText CategoryName;
        public Integer CategoryStartingLevel;
    }

    @UStruct
    public static class TrackDynamicBackground {
        public FSoftObjectPath BackgroundSubstance;
        public FLinearColor PrimaryColor;
        public FLinearColor SecondaryColor;
        public FLinearColor TertiaryColor;
        public Boolean bIsSpecial;
        public Boolean bIsFoil;
        public Integer MinimalDiscoveryLevel;
    }

    @UStruct
    public static class AthenaRewardSchedule {
        public List<AthenaRewardScheduleLevel> Levels;
    }

    @UStruct
    public static class AthenaRewardScheduleLevel {
        public List<AthenaRewardItemReference> Rewards;
    }

    @UStruct
    public static class AthenaSeasonBannerLevelSchedule {
        public List<AthenaSeasonBannerLevel> Levels;
    }

    @UStruct
    public static class AthenaSeasonBannerLevel {
        public FSoftObjectPath SurroundImage;
        public FSoftObjectPath BannerMaterial;
    }

    @UStruct
    public static class XpDisplayConversion {
        public FSoftObjectPath XpItemDef;
        public Integer ValueToReplaceAt;
    }

    @UStruct
    public static class AthenaMidSeasonUpdate {
        public Integer SeasonLevelRequirement;
        public Integer BookLevelRequirement;
        public Boolean SeasonPurchasedRequirement;
        public List<AthenaMidSeasonUpdateItemReq> ItemRequirements;
        public List<AthenaMidSeasonUpdateQuestReq> QuestRequirements;
        public AthenaRewardScheduleLevel Grants;
        public List<FSoftObjectPath> Removals;
    }

    @UStruct
    public static class AthenaMidSeasonUpdateItemReq {
        public FSoftObjectPath Item;
        public Integer Count;
    }

    @UStruct
    public static class AthenaMidSeasonUpdateQuestReq {
        public FSoftObjectPath Quest;
        public Boolean bCompletionRequired;
    }
}
