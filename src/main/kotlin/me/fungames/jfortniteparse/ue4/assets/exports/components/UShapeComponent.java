package me.fungames.jfortniteparse.ue4.assets.exports.components;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.exports.UClass;
import me.fungames.jfortniteparse.ue4.objects.core.math.FColor;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

public class UShapeComponent extends UPrimitiveComponent {
    public FPackageIndex /*BodySetup*/ ShapeBodySetup;
    public Lazy<UClass> AreaClass;
    public FColor ShapeColor;
    public Boolean bDrawOnlyIfSelected;
    public Boolean bShouldCollideWhenPlacing;
    public Boolean bDynamicObstacle;
}
