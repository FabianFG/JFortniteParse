package me.fungames.jfortniteparse.ue4.assets.exports;

import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class UBlueprintGeneratedClass_Properties extends UClassReal {
    public Integer NumReplicatedProperties;
    public Boolean bHasNativizedParent;
    public Boolean bHasCookedComponentInstancingData;
    public List<FPackageIndex /*DynamicBlueprintBinding*/> DynamicBindingObjects;
    public List<FPackageIndex /*ActorComponent*/> ComponentTemplates;
    public List<FPackageIndex /*TimelineTemplate*/> Timelines;
    //public List<FBPComponentClassOverride> ComponentClassOverrides;
    @UProperty(skipPrevious = 1)
    public FPackageIndex /*SimpleConstructionScript*/ SimpleConstructionScript;
    public FPackageIndex /*InheritableComponentHandler*/ InheritableComponentHandler;
    public FPackageIndex /*StructProperty*/ UberGraphFramePointerProperty;
    @UProperty(skipNext = 1)
    public FPackageIndex /*Function*/ UberGraphFunction;
    //public Map<FName, FBlueprintCookedComponentInstancingData> CookedComponentInstancingData;
}
