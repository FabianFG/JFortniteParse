package me.fungames.jfortniteparse.ue4.assets.exports.actors;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UObject;
import me.fungames.jfortniteparse.ue4.assets.exports.components.UActorComponent;
import me.fungames.jfortniteparse.ue4.assets.exports.components.USceneComponent;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

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
    public Boolean bActorIsBeingDestroyed;
    /*public EActorUpdateOverlapsMethod UpdateOverlapsMethodDuringLevelStreaming;
    public EActorUpdateOverlapsMethod DefaultUpdateOverlapsMethodDuringLevelStreaming;
    public ENetRole RemoteRole;
    public FRepMovement ReplicatedMovement;*/
    @UProperty(skipPrevious = 4)
    public Float InitialLifeSpan;
    public Float CustomTimeDilation;
    //public FRepAttachment AttachmentReplication;
    @UProperty(skipPrevious = 1)
    public Lazy<AActor> Owner;
    public FName NetDriverName;
    /*public ENetRole Role;
    public ENetDormancy NetDormancy;
    public ESpawnActorCollisionHandlingMethod SpawnCollisionHandlingMethod;
    public EAutoReceiveInput AutoReceiveInput;*/
    @UProperty(skipPrevious = 4)
    public Integer InputPriority;
    public FPackageIndex /*InputComponent*/ InputComponent;
    public Float NetCullDistanceSquared;
    public Integer NetTag;
    public Float NetUpdateFrequency;
    public Float MinNetUpdateFrequency;
    public Float NetPriority;
    public FPackageIndex /*Pawn*/ Instigator;
    public List<Lazy<AActor>> Children;
    public Lazy<USceneComponent> RootComponent;
    public List<FPackageIndex /*MatineeActor*/> ControllingMatineeActors;
    public List<FName> Layers;
    public FPackageIndex /*WeakObjectProperty ChildActorComponent*/ ParentComponent;
    public List<FName> Tags;
    /*public FScriptMulticastDelegate OnTakeAnyDamage;
    public FScriptMulticastDelegate OnTakePointDamage;
    public FScriptMulticastDelegate OnTakeRadialDamage;
    public FScriptMulticastDelegate OnActorBeginOverlap;
    public FScriptMulticastDelegate OnActorEndOverlap;
    public FScriptMulticastDelegate OnBeginCursorOver;
    public FScriptMulticastDelegate OnEndCursorOver;
    public FScriptMulticastDelegate OnClicked;
    public FScriptMulticastDelegate OnReleased;
    public FScriptMulticastDelegate OnInputTouchBegin;
    public FScriptMulticastDelegate OnInputTouchEnd;
    public FScriptMulticastDelegate OnInputTouchEnter;
    public FScriptMulticastDelegate OnInputTouchLeave;
    public FScriptMulticastDelegate OnActorHit;
    public FScriptMulticastDelegate OnDestroyed;
    public FScriptMulticastDelegate OnEndPlay;*/
    @UProperty(skipPrevious = 16)
    public List<Lazy<UActorComponent>> InstanceComponents;
    public List<Lazy<UActorComponent>> BlueprintCreatedComponents;

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
