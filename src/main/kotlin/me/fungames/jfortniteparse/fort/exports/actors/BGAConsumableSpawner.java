package me.fungames.jfortniteparse.fort.exports.actors;

import kotlin.Lazy;
import kotlin.UByte;
import me.fungames.jfortniteparse.fort.objects.FortItemEntry;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UClass;
import me.fungames.jfortniteparse.ue4.assets.exports.components.USceneComponent;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class BGAConsumableSpawner extends BuildingActor {
    public Lazy<USceneComponent> DummyRoot;
    public FName SpawnLootTierGroup;
    public Lazy<BuildingActor> AssociatedBuildingActor;
    public FPackageIndex /*EnvQuery*/ QueryTemplate;
    public List<FAIDynamicParam> QueryConfig;
    public Boolean bAlignSpawnedActorsToSurface;
    public List<FortItemEntry> ConsumablesToSpawn;

    @UStruct
    public static class FAIDynamicParam { // AIModule
        public FName ParamName;
        public EAIParamType ParamType;
        public Float Value;
        public FBlackboardKeySelector BBKey;
    }

    public enum EAIParamType {
        Float,
        Int,
        Bool
    }

    @UStruct
    public static class FBlackboardKeySelector {
        public List<FPackageIndex /*BlackboardKeyType*/> AllowedTypes;
        public FName SelectedKeyName;
        public Lazy<UClass> SelectedKeyType;
        public UByte SelectedKeyID;
        public Boolean bNoneIsAllowedValue;
    }
}
