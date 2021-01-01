package me.fungames.jfortniteparse.ue4.assets.exports;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.components.UActorComponent;
import me.fungames.jfortniteparse.ue4.assets.objects.FBPVariableMetaDataEntry;
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;

import java.util.List;

public class USCS_Node extends UObject {
    public Lazy<UClassReal> ComponentClass;
    public Lazy<UActorComponent> ComponentTemplate;
    public FBlueprintCookedComponentInstancingData CookedComponentInstancingData;
    public FName AttachToName;
    public FName ParentComponentOrVariableName;
    public FName ParentComponentOwnerClassName;
    public Boolean bIsParentComponentNative;
    public List<Lazy<USCS_Node>> ChildNodes;
    public List<FBPVariableMetaDataEntry> MetaDataArray;
    public FGuid VariableGuid;
    public FName InternalVariableName;

    @UStruct
    public static class FBlueprintCookedComponentInstancingData {
        public List<FBlueprintComponentChangedPropertyInfo> ChangedPropertyList;
        public Boolean bHasValidCookedData;
    }

    @UStruct
    public static class FBlueprintComponentChangedPropertyInfo {
        public FName PropertyName;
        public Integer ArrayIndex;
        public Lazy<UStruct> PropertyScope;
    }
}
