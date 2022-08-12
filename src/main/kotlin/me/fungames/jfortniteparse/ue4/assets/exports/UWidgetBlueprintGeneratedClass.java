package me.fungames.jfortniteparse.ue4.assets.exports;

import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class UWidgetBlueprintGeneratedClass extends UBlueprintGeneratedClass {
    public FPackageIndex WidgetTree;
    public Boolean bClassRequiresNativeTick;
    //public List<FDelegateRuntimeBinding> Bindings;
    @UProperty(skipPrevious = 1)
    public List<FPackageIndex> Animations;
    public List<FName> NamedSlots;
}
