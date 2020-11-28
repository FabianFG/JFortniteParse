package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.enums.EFortResourceType;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortDecoItemDefinition extends FortWeaponItemDefinition {
    public Boolean bReplacesEditedSurfaces;
    public FSoftObjectPath /*SoftClassPath*/ BlueprintClass;
    public FSoftObjectPath /*SoftClassPath*/ PlacementPreviewClass;
    public Boolean bCanBePlacedOnEnemyBuildings;
    public Float GridSnapSizeOverride;
    public Float RotationAngleIncrement;
    public Float GridPlacementOffset;
    public EPlacementType PlacementTypeOverride;
    public Boolean bForceIgnoreOverlapTest;
    public Boolean bIgnoreCollisionWithVehicles;
    public Boolean bForceIgnoreBuildingOverlaps;
    public Boolean bIgnoreCollisionWithCriticalActors;
    public Boolean bIgnoreCollisionWithStructuralGridActors;
    public Boolean bIgnoreCollisionWithFortStaticMeshActors;
    public Boolean bIgnoreCollisionWithPlayers;
    public Boolean bDisableLocationLerpWhilePlacing;
    public Boolean bDisableRotationLerpWhilePlacing;
    public Boolean bDisableScaleLerpWhilePlacing;
    public Boolean bAttachWhenPlacing;
    public Boolean bAllowPlacementOnWorldGeometry;
    public Boolean bAllowPlacementOnBuildings;
    public Boolean bDestroySmallObjectsWhenPlaced;
    public Boolean bSetOwningPlayerForSpawnedDeco;
    public Boolean bSetSpawnedDecoOnPlayerTeam;
    public Boolean bConsumeWhenPlaced;
    public Boolean bCancelToolWhenPlaced;
    public Boolean bCancelAbilityOnUnequip;
    public Boolean bRequiresPlayerPlaceableAttachmentActors;
    public Boolean bUseRelativeCameraRotation;
    public Boolean bAllowStairsWhenAttachingToFloors;
    public Boolean bSnapYawToHorizontalAxes;
    public Boolean bAllowAnyFloorPlacement;
    public Boolean bRequiresPermissionToEditWorld;
    public Boolean bAutoCreateAttachmentBuilding;
    public EFortResourceType AutoCreateAttachmentBuildingResourceType;
    public Integer MaxPlacementDistance;
    public List<FSoftObjectPath> AutoCreateAttachmentBuildingShapes;
    public List<FSoftObjectPath> AllowedShapes;
    public List<FSoftObjectPath> AllowedPlayerBuiltShapes;
    public Boolean bReplacesDecoOnAttachment;
    public Boolean bShowPreviewOnPressHeld;

    public enum EPlacementType {
        Free,
        Grid,
        None
    }
}
