package me.fungames.jfortniteparse.ue4.assets.exports.actors;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UObject;

public class AActor extends UObject {
    public FActorTickFunction PrimaryActorTick;
    public Boolean bNetTemporary;
    public Boolean bNetStartup;
    public Boolean bOnlyRelevantToOwner;
    public Boolean bAlwaysRelevant;
    public Boolean bReplicateMovement;
    public Boolean bHidden;
    public Boolean bTearOff;
    public Boolean bForceNetAddressable;
    public Boolean bExchangedRoles;
    public Boolean bNetLoadOnClient;
    public Boolean bNetUseOwnerRelevancy;
    public Boolean bRelevantForNetworkReplays;
    public Boolean bRelevantForLevelBounds;
    public Boolean bReplayRewindable;
    public Boolean bAllowTickBeforeBeginPlay;
    public Boolean bAutoDestroyWhenFinished;
    public Boolean bCanBeDamaged;
    public Boolean bBlockInput;
    public Boolean bCollideWhenPlacing;
    public Boolean bFindCameraComponentWhenViewTarget;
    public Boolean bGenerateOverlapEventsDuringLevelStreaming;
    public Boolean bIgnoresOriginShifting;
    public Boolean bEnableAutoLODGeneration;
    public Boolean bIsEditorOnlyActor;
    public Boolean bActorSeamlessTraveled;
    public Boolean bReplicates;
    public Boolean bCanBeInCluster;
    public Boolean bAllowReceiveTickEventOnDedicatedServer;
    public Boolean bActorEnableCollision;
    public Boolean bActorIsBeingDestroyed; // TODO continue

    @UStruct
    public static class FTickFunction {
        public ETickingGroup TickGroup;
        public ETickingGroup EndTickGroup;
        public Boolean bTickEvenWhenPaused;
        public Boolean bCanEverTick;
        public Boolean bStartWithTickEnabled;
        public Boolean bAllowTickOnDedicatedServer;
        public Float TickInterval;
    }

    public enum ETickingGroup {
        TG_PrePhysics,
        TG_StartPhysics,
        TG_DuringPhysics,
        TG_EndPhysics,
        TG_PostPhysics,
        TG_PostUpdateWork,
        TG_LastDemotable,
        TG_NewlySpawned,
    }

    @UStruct
    public static class FActorTickFunction extends FTickFunction {
    }
}
