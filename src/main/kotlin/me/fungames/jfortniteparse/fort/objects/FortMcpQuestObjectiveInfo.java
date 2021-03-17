package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.FDataTableRowHandle;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

@UStruct
public class FortMcpQuestObjectiveInfo {
    public FName BackendName;
    public List<FortQuestObjectiveStat> InlineObjectiveStats;
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

    @UStruct
    public static class FortQuestObjectiveStat {
        public List<InlineObjectiveStatTagCheckEntry> TagConditions;
        public String Condition;
        public List<String> TemplateIds;
        public EFortQuestObjectiveStatEvent Type;
        public Boolean bIsCached;
        public Boolean bHasInclusiveTargetTags;
        public Boolean bHasInclusiveSourceTags;
        public Boolean bHasInclusiveContextTags;
    }

    @UStruct
    public static class InlineObjectiveStatTagCheckEntry {
        public FGameplayTag Tag;
        public EInlineObjectiveStatTagCheckEntryType Type;
        public Boolean Require;
    }

    public enum EInlineObjectiveStatTagCheckEntryType {
        Target,
        Source,
        Context
    }

    public enum EFortQuestObjectiveStatEvent {
        Kill,
        TeamKill,
        KillContribution,
        Damage,
        SquadDamage,
        Visit,
        VisitDiscoverPOI,
        Land,
        Emote,
        Spray,
        Toy,
        Build,
        BuildingEdit,
        BuildingRepair,
        BuildingUpgrade,
        PlaceTrap,
        Complete,
        Craft,
        Collect,
        Win,
        Interact,
        TeamInteract,
        Destroy,
        Ability,
        WaveComplete,
        Custom,
        ComplexCustom,
        Client,
        AthenaRank,
        AthenaOutlive,
        RevivePlayer,
        Heal,
        EarnVehicleTrickPoints,
        VehicleAirTime,
        TimeElapsed,
        Death,
        AthenaMarker,
        PlacementUpdate,
        StormPhase,
        DistanceTraveled,
        DownOrElim,
        Accolade,
        TakeDamage,
        AthenaCollection,
        UsedNPCService,
        ReceivedNPCGift,
        InitiatedNPCConversation,
        AthenaCraft,
        AthenaTurnInQuest,
        NumGameplayEvents,
        Acquire,
        Consume,
        OpenCardPack,
        PurchaseCardPack,
        Convert,
        Upgrade,
        UpgradeRarity,
        QuestComplete,
        AssignWorker,
        CollectExpedition,
        CollectSuccessfulExpedition,
        LevelUpCollectionBook,
        LevelUpAthenaSeason,
        LevelUpBattlePass,
        GainAthenaSeasonXp,
        HasItem,
        HasAccumulatedItem,
        SlotInCollection,
        AlterationRespec,
        AlterationUpgrade,
        HasCompletedQuest,
        HasAssignedWorker,
        HasUpgraded,
        HasConverted,
        HasUpgradedRarity,
        HasLeveledUpCollectionBook,
        SlotHeroInLoadout,
        HasLeveledUpAthenaSeason,
        HasLeveledUpBattlePass,
        HasGainedAthenaSeasonXp,
        MinigameDynamicEvent,
        MinigameComplete,
        MinigameDeath,
        MinigameAssist,
        Max_None
    }

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
