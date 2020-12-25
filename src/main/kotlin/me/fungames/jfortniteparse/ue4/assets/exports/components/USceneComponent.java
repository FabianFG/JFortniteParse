package me.fungames.jfortniteparse.ue4.assets.exports.components;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.enums.EDetailMode;
import me.fungames.jfortniteparse.ue4.objects.core.math.FRotator;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.uobject.FMulticastScriptDelegate;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class USceneComponent extends UActorComponent {
    public FPackageIndex /*WeakObjectProperty PhysicsVolume*/ PhysicsVolume;
    public Lazy<USceneComponent> AttachParent;
    public FName AttachSocketName;
    public List<Lazy<USceneComponent>> AttachChildren;
    public List<Lazy<USceneComponent>> ClientAttachedChildren;
    public FVector RelativeLocation;
    public FRotator RelativeRotation;
    public FVector RelativeScale3D;
    public FVector ComponentVelocity;
    public Boolean bComponentToWorldUpdated;
    public Boolean bAbsoluteLocation;
    public Boolean bAbsoluteRotation;
    public Boolean bAbsoluteScale;
    public Boolean bVisible;
    public Boolean bShouldBeAttached;
    public Boolean bShouldSnapLocationWhenAttached;
    public Boolean bShouldSnapRotationWhenAttached;
    public Boolean bShouldUpdatePhysicsVolume;
    public Boolean bHiddenInGame;
    public Boolean bBoundsChangeTriggersStreamingDataRebuild;
    public Boolean bUseAttachParentBound;
    //public EComponentMobility Mobility;
    @UProperty(skipPrevious = 1)
    public EDetailMode DetailMode;
    public FMulticastScriptDelegate PhysicsVolumeChangedDelegate;
}
