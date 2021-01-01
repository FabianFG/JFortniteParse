package me.fungames.jfortniteparse.fort.exports.actors;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.exports.UStaticMesh;
import me.fungames.jfortniteparse.ue4.assets.exports.actors.AActor;
import me.fungames.jfortniteparse.ue4.assets.exports.components.UStaticMeshComponent;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.Map;

public class FortHLODSMActor extends AActor {
    public FName ActorFName;
    public FVector UniqueWorldLocation;
    public Boolean bIsDynamic;
    public Map<Lazy<UStaticMesh>, FPackageIndex /*InstancedStaticMeshComponent*/> ImposterComponents;
    public Float MaxDrawDistanceMultiplier;
    public FSoftObjectPath HLODStaticMesh;
    public Lazy<UStaticMeshComponent> StaticMeshComponent;
}
