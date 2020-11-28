package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.enums.EFortCustomGender;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class MyTownData extends UDataAsset {
    public List<MyTownWorkerPersonalityData> WorkerPersonalities;
    public List<MyTownWorkerSetBonusData> WorkerSetBonuses;
    public FPackageIndex /*FortPersistentResourceItemDefinition*/ PersonnelXpItemDefinition;
    public FPackageIndex /*FortPersistentResourceItemDefinition*/ HeroXpItemDefinition;
    public FPackageIndex /*FortPersistentResourceItemDefinition*/ VoucherItemDefinition;
    public FPackageIndex /*FortPersistentResourceItemDefinition*/ SchematicXpItemDefinition;
    public FPackageIndex /*FortCurrencyItemDefinition*/ CurrencyItemDefinition;
    public FPackageIndex /*FortTokenType*/ SkillPointItemDefinition;
    public FPackageIndex /*FortTokenType*/ ResearchPointItemDefinition;
    public FPackageIndex /*Class*/ TotalRatingGameplayEffect;

    @UStruct
    public static class MyTownWorkerPersonalityData {
        public FGameplayTagContainer PersonalityTypeTag;
        public FText PersonalityName;
        public Integer SelectionWeight;
        public List<MyTownWorkerGenderData> GenderData;
    }

    @UStruct
    public static class MyTownWorkerGenderData {
        public EFortCustomGender Gender;
        public Integer SelectionWeight;
        public List<MyTownWorkerPortraitData> PotraitData;
    }

    @UStruct
    public static class MyTownWorkerPortraitData {
        public FSoftObjectPath Portrait;
        public Integer SelectionWeight;
    }

    @UStruct
    public static class MyTownWorkerSetBonusData {
        public FGameplayTagContainer SetBonusTypeTag;
        public FText DisplayName;
        public Integer RequiredWorkersCount;
        public FPackageIndex /*Class*/ SetBonusEffect;
        public Integer SelectionWeight;
    }
}
