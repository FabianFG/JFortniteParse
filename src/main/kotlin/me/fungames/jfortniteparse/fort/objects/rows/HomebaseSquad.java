package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.fort.enums.EFortItemType;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.FCurveTableRowHandle;
import me.fungames.jfortniteparse.ue4.objects.FGameplayAttribute;
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class HomebaseSquad extends FTableRowBase {
    public FText DisplayName;
    public FText ShortDisplayName;
    public EFortHomebaseSquadType SquadType;
    public FGameplayTag ManagerSynergyTag;
    public List<HomebaseSquadSlot> CrewSlots;
    public FGameplayTagContainer RequiredTheaterTags;
    public int MaxNumDefendersAllowedInLevel;
    public int MaxNumDefendersAllowedInGroupLevel;
    public boolean bConsiderNumPlayersForMaxNumDefenders;
    public boolean bAlwaysRemoveOldestDefenderWhenReplacing;

    @UStruct
    public static class HomebaseSquadSlot {
        public FText DisplayName;
        public List<EFortItemType> ValidSlottableItemTypes; // List<EFortItemType>
        public FGameplayTagContainer TagFilter;
        public List<HomebaseSquadAttributeBonus> SlottingBonuses;
        public FPackageIndex /*CurveTable*/ PersonalityMatchBonusTable;
        public ESquadSlotType SlotType;
    }

    @UStruct
    public static class HomebaseSquadAttributeBonus {
        public FGameplayAttribute AttributeGranted;
        public FCurveTableRowHandle BonusCurve;
    }

    public enum EFortHomebaseSquadType {
        AttributeSquad, CombatSquad, DefenderSquad, ExpeditionSquad
    }

    public enum ESquadSlotType {
        HeroSquadMissionDefender, SurvivorSquadLeadSurvivor, SurvivorSquadSurvivor, DefenderSquadMember, ExpeditionSquadMember
    }
}
