package me.fungames.jfortniteparse.fort.exports;

import kotlin.Lazy;
import me.fungames.jfortniteparse.fort.enums.EFortCustomGender;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UCurveTable;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortHomebaseManager extends UDataAsset {
    public FPackageIndex /*FortHomebaseNodeGameplayEffectDataTable*/ HomebaseNodeGameplayEffectDataTable;
    public Lazy<UCurveTable> ResearchSystemUpgradesTable;
    public FPackageIndex /*Class*/ StatsGamplayEffect;
    public Lazy<UDataTable> HomebaseSquadDataTable;
    public Lazy<UDataTable> ExpeditionSlotsDataTable;
    public Lazy<UCurveTable> ManagerSquadSynergyBonusTable;
    public FPackageIndex /*Class*/ SquadGE;
    public List<WorkerPersonalityData> WorkerPersonalities;
    public List<WorkerSetBonusData> WorkerSetBonuses;
    public List<ManagerSynergyData> ManagerSynergies;

    @UStruct
    public static class WorkerPersonalityData {
        public FGameplayTag PersonalityTypeTag;
        public FText PersonalityName;
        public Integer SelectionWeight;
        public List<WorkerGenderData> GenderData;
    }

    @UStruct
    public static class WorkerGenderData {
        public EFortCustomGender Gender;
        public List<WorkerPortraitData> PotraitData;
    }

    @UStruct
    public static class WorkerPortraitData {
        public FSoftObjectPath Portrait;
    }

    @UStruct
    public static class WorkerSetBonusData {
        public FGameplayTag SetBonusTypeTag;
        public FText DisplayName;
        public Integer RequiredWorkersCount;
        public FPackageIndex /*Class*/ SetBonusEffect;
        public Integer SelectionWeight;
        public Integer PowerPoints;
    }

    @UStruct
    public static class ManagerSynergyData {
        public FGameplayTag SynergyTypeTag;
        public List<WorkerGenderData> GenderData;
    }
}
