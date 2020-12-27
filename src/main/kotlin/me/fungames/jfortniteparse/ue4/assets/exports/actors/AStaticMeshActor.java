package me.fungames.jfortniteparse.ue4.assets.exports.actors;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.exports.components.UStaticMeshComponent;

public class AStaticMeshActor extends AActor {
    public Lazy<UStaticMeshComponent> StaticMeshComponent;
    public Boolean bStaticMeshReplicateMovement;
    public ENavDataGatheringMode NavigationGeometryGatheringMode;

    public enum ENavDataGatheringMode {
        Default,
        Instant,
        Lazy
    }
}
