package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.FDataTableRowHandle;
import me.fungames.jfortniteparse.fort.objects.FortHiddenRewardQuantityPair;
import me.fungames.jfortniteparse.fort.objects.FortItemQuantityPair;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortQuestItemDefinition extends FortAccountItemDefinition {
    public EFortQuestType QuestType;
    public EFortQuestSubtype QuestSubtype;
    public Boolean UnknownData00;
    public Boolean bShouldDisplayOverallQuestInformation;
    public Boolean bAthenaUpdateObjectiveOncePerMatch;
    public Boolean bAthenaMustCompleteInSingleMatch;
    public Boolean bUpdateObjectiveOncePerMatch;
    public Boolean IsStreamingRequired;
    public Boolean bExpandsStormShield;
    public Boolean bHidden;
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
    public List<FortItemQuantityPair> Rewards;
    public UDataTable RewardsTable;
    public String QuestPool;
    public List<FortHiddenRewardQuantityPair> HiddenRewards;
    public List<String> FeatureRewards;
    //public List<FortMcpQuestRewardInfo> SelectableRewards;
    @UProperty(skipPrevious = 1)
    public List<FortMcpQuestObjectiveInfo> Objectives;
    public FGameplayTagContainer Prerequisites;
    public FSoftObjectPath PrerequisiteQuest;
    public FDataTableRowHandle PrerequisiteObjective;
    public Float Weight;
    public Short /*uint16_t*/ GranterWindowPeriodMinutes;
    public Short /*uint16_t*/ GranterCooldownPeriodSeconds;
    public FDataTableRowHandle Category;
    public FSoftObjectPath IntroConversation;
    public FSoftObjectPath SelectRewardsConversation;
    public FSoftObjectPath ClaimConversation;
    public FText RewardHeaderText;
    public FText RewardDescription;
    public FText CompletionText;
    //public List<FortQuestMissionCreationContext> MissionCreationContexts;
    //public FortMissionConfigDataParams MissionConfigMetadata;
    @UProperty(skipPrevious = 2)
    public Integer /*uint32_t*/ ClaimPriority;
    public Integer SortPriority;
    public FSoftObjectPath QuestAbilitySet;

    public enum EFortQuestType {
        Task, Optional, DailyQuest, TransientQuest, SurvivorQuest, Achievement, Onboarding, StreamBroadcaster, StreamViewer, StreamSubscriber, Athena, AthenaDailyQuest, AthenaEvent, AthenaChallengeBundleQuest, All
    }

    public enum EFortQuestSubtype {
        None, WeeklyChallenge, PunchCard, QuickChallenge
    }

    @UStruct
    public static class FortMcpQuestObjectiveInfo {
        public FName BackendName;
        //public List<FortQuestObjectiveStat> InlineObjectiveStats;
        @UProperty(skipPrevious = 1)
        public FDataTableRowHandle ObjectiveStatHandle;
        public List<FDataTableRowHandle> AlternativeStatHandles;
        public EFortQuestObjectiveItemEvent ItemEvent;
        public Boolean bHidden;
        public Boolean bRequirePrimaryMissionCompletion;
        public Boolean bCanProgressInZone;
        public Boolean bDisplayDynamicAnnouncementUpdate;
        public EObjectiveStatusUpdateType DynamicStatusUpdateType;
        public EFortInventoryFilter LinkVaultTab;
        public EFortFrontendInventoryFilter LinkToItemManagement;
        public FSoftObjectPath ItemReference;
        public String ItemTemplateIdOverride;
        public FName LinkSquadID;
        public Integer LinkSquadIndex;
        public FText Description;
        public FText HudShortDescription;
        public FSoftObjectPath HudIcon;
        public Integer Count;
        public Integer Stage;
        public Integer DynamicStatusUpdatePercentInterval;
        public Float DynamicUpdateCompletionDelay;
        public FSoftObjectPath ScriptedAction;
        public FSoftObjectPath FrontendScriptedAction;

        public enum EFortQuestObjectiveItemEvent {
            Craft, Collect, Acquire, Consume, OpenCardPack, PurchaseCardPack, Convert, Upgrade, UpgradeRarity, QuestComplete, AssignWorker, LevelUpCollectionBook, LevelUpAthenaSeason, LevelUpBattlePass, GainAthenaSeasonXp, HasItem, HasAccumulatedItem, SlotInCollection, AlterationRespec, AlterationUpgrade, HasCompletedQuest, HasAssignedWorker, HasUpgraded, HasConverted, HasUpgradedRarity, HasLeveledUpCollectionBook, SlotHeroInLoadout, HasLeveledUpAthenaSeason, HasLeveledUpBattlePass, HasGainedAthenaSeasonXp, MinigameTime, Max_None
        }

        public enum EObjectiveStatusUpdateType {
            Always, OnPercent, OnComplete, Never
        }

        public enum EFortInventoryFilter {
            WeaponMelee, WeaponRanged, Ammo, Traps, Consumables, Ingredients, Gadget, Decorations, Badges, Heroes, LeadSurvivors, Survivors, Defenders, Resources, ConversionControl, AthenaCosmetics, Playset, CreativePlot, TeamPerk, Workers, Invisible, Max_None
        }

        public enum EFortFrontendInventoryFilter {
            Schematics, WorldItems, WorldItemsInGame, WorldItemsStorage, WorldItemsTransfer, ConsumablesAndAccountResources, Heroes, Defenders, Survivors, AthenaCharacter, AthenaBackpack, AthenaPickaxe, AthenaGliders, AthenaContrails, AthenaEmotes, AthenaItemWraps, AthenaLoadingScreens, AthenaLobbyMusic, AthenaCharm, HestiaWeapons, HestiaResources, StarlightInventory, Invisible, Max_None
        }
    }
}
