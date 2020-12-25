package me.fungames.jfortniteparse.fort.exports.actors;

import kotlin.Lazy;
import kotlin.UInt;
import me.fungames.jfortniteparse.fort.enums.EBuildingWallArea;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.USoundBase;
import me.fungames.jfortniteparse.ue4.assets.exports.UStaticMesh;
import me.fungames.jfortniteparse.ue4.assets.exports.components.UStaticMeshComponent;
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface;
import me.fungames.jfortniteparse.ue4.objects.core.math.FRotator;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class BuildingWall extends BuildingSMActor {
    public FSoftObjectPath /*SoftClassPath*/ DoorObstacleClass;
    public FClimbLinkData ClimbLink;
    public FVector DoorOffset;
    public FVector SlidingDoorOffset;
    public FVector AutomaticSlidingDoorBoxExtent;
    public FVector AutomaticSlidingDoorBoxOffset;
    public FVector SlamOpenDoorBoxExtent;
    public Float SlidingTranslation;
    public Float SlidingOpenTime;
    public Lazy<USoundBase> DoorOpeningSound;
    public Lazy<USoundBase> DoorSlammedOpenSound;
    public Lazy<USoundBase> DoorClosingSound;
    public Lazy<UMaterialInterface> DoorAnimatingMaterial;
    public Lazy<UStaticMesh> DoorMesh;
    public Lazy<UStaticMesh> DoubleDoorMesh;
    public Lazy<UStaticMeshComponent> DoorComponent;
    public Lazy<UStaticMeshComponent> SlidingDoorComponent;
    public Lazy<UStaticMeshComponent> DoubleDoorComponent;
    public FPackageIndex /*BoxComponent*/ DoorBoxComponent;
    public FPackageIndex /*FortDoorLinkComponent*/ DoorSmartLinkComp;
    public Lazy<UStaticMeshComponent> DoorBlueprintMeshComp;
    public Lazy<UStaticMeshComponent> SlidingDoorBlueprintMeshComp;
    public Lazy<UStaticMeshComponent> DoubleDoorBlueprintMeshComp;
    public FRotator DoorDesiredRotOffset;
    public Float DoorDesiredXLocation;
    public Float SlidingDoorDesiredXLocation;
    public FBuildingActorNavArea AreaPatternOverride;
    public Float AreaWidthOverride;
    public EBuildingWallArea AreaShapeType;
    public EDoorOpenStyle DoorOpenStyle;
    public Boolean bSwingingDoor;
    public Boolean bSlidingDoor;
    public Boolean bAutomaticSlidingDoor;
    public Boolean bDoubleDoor;
    public Boolean bCreateDoorLink;
    public Boolean bDoorOpen;
    public Boolean bLocalDoorOpen;
    public Boolean bDoorCollisionDisabled;
    public Boolean bLocalDoorCollisionDisabled;
    public Boolean bOverrideAreaWidth;
    public Boolean bCreateClimbLink;
    public Boolean bProhibitPassOverLowEndOfTriangleWall;

    @UStruct
    public static class FClimbLinkData {
        public UInt UniqueLinkId;
    }

    @UStruct
    public static class FBuildingActorNavArea {
        public Integer AreaBits;
    }

    public enum EDoorOpenStyle {
        Open,
        SlamOpen,
        SmashOpen,
        Close
    }
}
