package me.fungames.jfortniteparse.ue4.assets.exports.components;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UObject;
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class UActorComponent extends UObject {
    //public FActorComponentTickFunction PrimaryComponentTick;
    @UProperty(skipPrevious = 1)
    public List<FName> ComponentTags;
    public List<FPackageIndex /*AssetUserData*/> AssetUserData;
    public Integer UCSSerializationIndex;
    public Boolean bNetAddressable;
    public Boolean bReplicates;
    public Boolean bAutoActivate;
    public Boolean bIsActive;
    public Boolean bEditableWhenInherited;
    public Boolean bCanEverAffectNavigation;
    public Boolean bIsEditorOnly;
    public EComponentCreationMethod CreationMethod;
    /*public FScriptMulticastDelegate OnComponentActivated;
    public FScriptMulticastDelegate OnComponentDeactivated;*/
    @UProperty(skipPrevious = 2)
    public List<FSimpleMemberReference> UCSModifiedProperties;

    public enum EComponentCreationMethod {
        Native,
        SimpleConstructionScript,
        UserConstructionScript,
        Instance
    }

    @UStruct
    public static class FSimpleMemberReference {
        public Lazy<UObject> MemberParent;
        public FName MemberName;
        public FGuid MemberGuid;
    }
}
