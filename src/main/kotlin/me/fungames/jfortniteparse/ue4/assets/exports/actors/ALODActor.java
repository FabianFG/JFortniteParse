package me.fungames.jfortniteparse.ue4.assets.exports.actors;

import kotlin.Lazy;
import kotlin.UByte;
import me.fungames.jfortniteparse.ue4.assets.exports.components.UStaticMeshComponent;
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;
import java.util.Map;

public class ALODActor extends AActor {
    public Lazy<UStaticMeshComponent> StaticMeshComponent;
    public Map<Lazy<UMaterialInterface>, FPackageIndex /*InstancedStaticMeshComponent*/> ImpostersStaticMeshComponents;
    public FPackageIndex /*HLODProxy*/ Proxy;
    public FName Key;
    public Float LODDrawDistance;
    public Integer LODLevel;
    public List<Lazy<AActor>> SubActors;
    public UByte CachedNumHLODLevels;
}
