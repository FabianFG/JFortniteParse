package me.fungames.jfortniteparse.ue4.assets.exports;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.exports.components.UActorComponent;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class UBlueprintGeneratedClass_Properties extends UClassReal {
    public Integer NumReplicatedProperties;
    public Boolean bHasNativizedParent;
    public Boolean bHasCookedComponentInstancingData;
    public List<FPackageIndex /*DynamicBlueprintBinding*/> DynamicBindingObjects;
    public List<Lazy<UActorComponent>> ComponentTemplates;
    public List<Lazy<UTimelineTemplate>> Timelines;
    //public List<FBPComponentClassOverride> ComponentClassOverrides;
    @UProperty(skipPrevious = 1)
    public FPackageIndex /*SimpleConstructionScript*/ SimpleConstructionScript;
    public FPackageIndex /*InheritableComponentHandler*/ InheritableComponentHandler;
    public FPackageIndex /*StructProperty*/ UberGraphFramePointerProperty;
    @UProperty(skipNext = 1)
    public Lazy<UFunction> UberGraphFunction;
    //public Map<FName, FBlueprintCookedComponentInstancingData> CookedComponentInstancingData;
}
