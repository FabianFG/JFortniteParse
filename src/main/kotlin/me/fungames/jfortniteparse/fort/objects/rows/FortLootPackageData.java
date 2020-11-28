package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortLootPackageData extends FTableRowBase {
    public FName LootPackageID;
    public float Weight;
    public FName NamedWeightMult;
    public List<FName> PotentialNamedWeights;
    public int Count;
    public int LootPackageCategory;
    public FGameplayTagContainer GameplayTags;
    public FName RequiredTag;
    public String LootPackageCall;
    public FSoftObjectPath ItemDefinition;
    public String PersistentLevel;
    public int MinWorldLevel;
    public int MaxWorldLevel;
    public boolean bAllowBonusDrops;
    public String Annotation;
}
