package me.fungames.jfortniteparse.ue4.assets.exports.actors;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.enums.ETickingGroup;
import me.fungames.jfortniteparse.ue4.assets.exports.UObject;
import me.fungames.jfortniteparse.ue4.assets.exports.components.UActorComponent;
import me.fungames.jfortniteparse.ue4.assets.exports.components.USceneComponent;
import me.fungames.jfortniteparse.ue4.objects.uobject.FMulticastScriptDelegate;
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
    public ENetRole Role;
    public ENetDormancy NetDormancy;
    public ESpawnActorCollisionHandlingMethod SpawnCollisionHandlingMethod;
    public EAutoReceiveInput AutoReceiveInput;
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
    public FMulticastScriptDelegate OnTakeAnyDamage;
    public FMulticastScriptDelegate OnTakePointDamage;
    public FMulticastScriptDelegate OnTakeRadialDamage;
    public FMulticastScriptDelegate OnActorBeginOverlap;
    public FMulticastScriptDelegate OnActorEndOverlap;
    public FMulticastScriptDelegate OnBeginCursorOver;
    public FMulticastScriptDelegate OnEndCursorOver;
    public FMulticastScriptDelegate OnClicked;
    public FMulticastScriptDelegate OnReleased;
    public FMulticastScriptDelegate OnInputTouchBegin;
    public FMulticastScriptDelegate OnInputTouchEnd;
    public FMulticastScriptDelegate OnInputTouchEnter;
    public FMulticastScriptDelegate OnInputTouchLeave;
    public FMulticastScriptDelegate OnActorHit;
    public FMulticastScriptDelegate OnDestroyed;
    public FMulticastScriptDelegate OnEndPlay;
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

    @UStruct
    public static class FActorTickFunction extends FTickFunction {
    }

    public enum ENetRole {
        ROLE_None,
        ROLE_SimulatedProxy,
        ROLE_AutonomousProxy,
        ROLE_Authority
    }

    public enum ENetDormancy {
        DORM_Never,
        DORM_Awake,
        DORM_DormantAll,
        DORM_DormantPartial,
        DORM_Initial
    }

    public enum ESpawnActorCollisionHandlingMethod {
        Undefined,
        AlwaysSpawn,
        AdjustIfPossibleButAlwaysSpawn,
        AdjustIfPossibleButDontSpawnIfColliding,
        DontSpawnIfColliding
    }

    public enum EAutoReceiveInput {
        Disabled,
        Player0,
        Player1,
        Player2,
        Player3,
        Player4,
        Player5,
        Player6,
        Player7
    }
}
