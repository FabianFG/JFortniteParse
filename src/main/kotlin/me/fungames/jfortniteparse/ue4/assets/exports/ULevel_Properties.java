package me.fungames.jfortniteparse.ue4.assets.exports;

import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.objects.core.math.FIntVector;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class ULevel_Properties extends UObject {
    public FPackageIndex /*World*/ OwningWorld;
    public FPackageIndex /*Model*/ Model;
    public List<FPackageIndex /*ModelComponent*/> ModelComponents;
    public FPackageIndex /*LevelActorContainer*/ ActorCluster;
    public Integer NumTextureStreamingUnbuiltComponents;
    public Integer NumTextureStreamingDirtyResources;
    public FPackageIndex /*LevelScriptActor*/ LevelScriptActor;
    public FPackageIndex /*NavigationObjectBase*/ NavListStart;
    public FPackageIndex /*NavigationObjectBase*/ NavListEnd;
    public List<FPackageIndex /*NavigationDataChunk*/> NavDataChunks;
    public Float LightmapTotalSize;
    public Float ShadowmapTotalSize;
    public List<FVector> StaticNavigableGeometry;
    public List<FGuid> StreamingTextureGuids;
    public FGuid LevelBuildDataId;
    public FPackageIndex /*MapBuildDataRegistry*/ MapBuildData;
    public FIntVector LightBuildLevelOffset;
    public Boolean bIsLightingScenario;
    public Boolean bTextureStreamingRotationChanged;
    public Boolean bStaticComponentsRegisteredInStreamingManager;
    public Boolean bIsVisible;
    public FPackageIndex /*WorldSettings*/ WorldSettings;
    @UProperty(skipNext = 1)
    public List<FPackageIndex /*AssetUserData*/> AssetUserData;
    //public List<FReplicatedStaticActorDestructionInfo> DestroyedReplicatedStaticActors;
}
