package me.fungames.jfortniteparse.ue4.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

@UStruct
public class FGameplayAttribute {
    public String AttributeName;
    public FFieldPath Attribute;
    public FPackageIndex /*UStruct*/ AttributeOwner;
}
