package me.fungames.jfortniteparse.fort.exports;

import kotlin.UInt;
import kotlin.UShort;
import me.fungames.jfortniteparse.fort.objects.FortHiddenRewardQuantityPair;
import me.fungames.jfortniteparse.fort.objects.FortItemQuantityPair;
import me.fungames.jfortniteparse.fort.objects.FortMcpQuestObjectiveInfo;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.FDataTableRowHandle;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortQuestItemDefinition extends FortAccountItemDefinition {
    public EFortQuestType QuestType;
    public EFortQuestSubtype QuestSubtype;
    public Boolean bShouldDisplayOverallQuestInformation;
    public Boolean bAthenaUpdateObjectiveOncePerMatch;
    public Boolean bAthenaGrantRarityToken;
    public Boolean bAthenaMustCompleteInSingleMatch;
    public Boolean bUpdateObjectiveOncePerMatch;
    public Boolean IsStreamingRequired;
    public Boolean bExpandsStormShield;
    public Boolean bHidden;
    public FSoftObjectPath TandemCharacterData;
    public Boolean bSuppressQuestGrantedEvent;
    public Boolean bInitiallySuppressedReplacementQuest;
    public Boolean bIncludedInCategories;
    public Boolean bAutoLaunch;
    public Boolean bDeprecated;
    public Boolean bDisableBackendConditionEvaluation;
    public Boolean bAllowTileMatching;
    public Boolean bAllowPlayNowNavigation;
    public Boolean bAllowMissionAlertMatchesBypassingTileRequirements;
    public Boolean bTutorialQuest;
    public Boolean bHideStageDescription;
    public Boolean bHideIncompleteObjectiveLocations;
    public Integer ExpirationDuration;
    public Integer ObjectiveCompletionCount;
    public Integer Threshold;
    public List<FortItemQuantityPair> Rewards;
    public FPackageIndex /*DataTable*/ RewardsTable;
    public String QuestPool;
    public List<FortHiddenRewardQuantityPair> HiddenRewards;
    public List<String> FeatureRewards;
    public List<FortMcpQuestRewardInfo> SelectableRewards;
    public List<FortMcpQuestObjectiveInfo> Objectives;
    public List<FSoftObjectPath> TransientPrerequisiteQuests;
    public Boolean bGrantTransientQuestToSquad;
    public Boolean bTransientAutoComplete;
    public Boolean bAllowMultipleCompletionsPerMatch;
    public FGameplayTagContainer Prerequisites;
    public FSoftObjectPath PrerequisiteQuest;
    public FDataTableRowHandle PrerequisiteObjective;
    public Float Weight;
    public UShort GranterWindowPeriodMinutes;
    public UShort GranterCooldownPeriodSeconds;
    public FDataTableRowHandle Category;
    public FSoftObjectPath IntroConversation;
    public FSoftObjectPath SelectRewardsConversation;
    public FSoftObjectPath ClaimConversation;
    public FText RewardHeaderText;
    public FText RewardDescription;
    public FText CompletionText;
    public List<FortQuestMissionCreationContext> MissionCreationContexts;
    public FortMissionConfigDataParams MissionConfigMetadata;
    public UInt ClaimPriority;
    public Integer SortPriority;
    public FSoftObjectPath QuestAbilitySet;
    public Boolean bForceExpiryExport;

    public enum EFortQuestType {
        Task,
        Optional,
        DailyQuest,
        TransientQuest,
        SurvivorQuest,
        Achievement,
        Onboarding,
        StreamBroadcaster,
        StreamViewer,
        StreamSubscriber,
        Athena,
        AthenaDailyQuest,
        AthenaEvent,
        AthenaChallengeBundleQuest,
        AthenaTransientQuest,
        All
    }

    public enum EFortQuestSubtype {
        None,
        WeeklyChallenge,
        PunchCard,
        QuickChallenge,
        Milestone,
        UrgentQuest
    }

    @UStruct
    public static class FortMcpQuestRewardInfo {
        public List<FortItemQuantityPair> Rewards;
    }

    @UStruct
    public static class FortQuestMissionCreationContext {
        public FSoftObjectPath MissionInfo;
        public List<FGameplayTagContainer> MissionCreationContextTags;
        public Boolean bSetQuestOwnerAsMissionOwner;
        public Integer MaxNumberToSpawnInWorld;
    }

    @UStruct
    public static class FortMissionConfigDataParams {
        public List<FortMissionConfigDataBucket> ConfigParams;
    }

    @UStruct
    public static class FortMissionConfigDataBucket {
        public FGameplayTag Tag;
        public FSoftObjectPath /*SoftClassPath*/ ConfigDataClass;
    }
}
