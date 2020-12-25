package me.fungames.jfortniteparse.ue4.assets.exports;

import me.fungames.jfortniteparse.ue4.assets.objects.meshes.FStaticMaterial;
import me.fungames.jfortniteparse.ue4.objects.core.math.FBoxSphereBounds;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.engine.FPerPlatformInt;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class UStaticMesh_Properties extends UStreamableRenderAsset {
    public FPerPlatformInt MinLOD;
    public Float LpvBiasMultiplier;
    public List<FStaticMaterial> StaticMaterials;
    public Float LightmapUVDensity;
    public Integer LightMapResolution;
    public Integer LightMapCoordinateIndex;
    public Float DistanceFieldSelfShadowBias;
    public FPackageIndex /*BodySetup*/ BodySetup;
    public Integer LODForCollision;
    public Boolean bGenerateMeshDistanceField;
    public Boolean bStripComplexCollisionForConsole;
    public Boolean bHasNavigationData;
    public Boolean bSupportUniformlyDistributedSampling;
    public Boolean bSupportPhysicalMaterialMasks;
    public Boolean bIsBuiltAtRuntime;
    public Boolean bAllowCPUAccess;
    public Boolean bSupportGpuUniformlyDistributedSampling;
    public List<FPackageIndex /*StaticMeshSocket*/> Sockets;
    public FVector PositiveBoundsExtension;
    public FVector NegativeBoundsExtension;
    public FBoxSphereBounds ExtendedBounds;
    public Integer ElementToIgnoreForTexFactor;
    public List<FPackageIndex /*AssetUserData*/> AssetUserData;
    public FPackageIndex /*Object*/ EditableMesh;
    public FPackageIndex /*NavCollisionBase*/ NavCollision;
}
