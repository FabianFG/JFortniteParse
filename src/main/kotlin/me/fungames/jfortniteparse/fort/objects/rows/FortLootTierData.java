package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;

import java.util.List;

public class FortLootTierData extends FTableRowBase {
    public FName TierGroup;
    public float Weight;
    public ELootQuotaLevel QuotaLevel;
    public int LootTier;
    public int MinWorldLevel;
    public int MaxWorldLevel;
    public String StreakBreakerCurrency;
    public int StreakBreakerPointsMin;
    public int StreakBreakerPointsMax;
    public int StreakBreakerPointsSpend;
    public FName LootPackage;
    public FName LootPreviewPackage;
    public float NumLootPackageDrops;
    public List<Integer> LootPackageCategoryWeightArray;
    public List<Integer> LootPackageCategoryMinArray;
    public List<Integer> LootPackageCategoryMaxArray;
    public FGameplayTagContainer GameplayTags;
    public FGameplayTagContainer RequiredGameplayTags;
    public boolean bAllowBonusLootDrops;
    public String Annotation;

    public enum ELootQuotaLevel {
        Unlimited,
        Level1,
        Level2,
        Level3,
        Level4,
        Level5,
        Level6,
        Level7,
        Level8,
        Level9,
        Level10,
        Level11,
        Level12,
        Level13,
        Level14,
        Level15,
        Level16,
        Level17,
        NumLevels
    }
}
