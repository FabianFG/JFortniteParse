package me.fungames.jfortniteparse.ue4.assets.exports.actors;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.math.FColor;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class ABrush extends AActor {
    public EBrushType BrushType;
    public FColor BrushColor;
    public Integer PolyFlags;
    public Boolean bColored;
    public Boolean bSolidWhenSelected;
    public Boolean bPlaceableFromClassBrowser;
    public Boolean bNotForClientOrServer;
    public FPackageIndex /*Model*/ Brush;
    public FPackageIndex /*BrushComponent*/ BrushComponent;
    public Boolean bInManipulation;
    public List<FGeomSelection> SavedSelections;

    public enum EBrushType {
        Brush_Default,
        Brush_Add,
        Brush_Subtract
    }

    @UStruct
    public static class FGeomSelection {
        public Integer Type;
        public Integer Index;
        public Integer SelectionIndex;
    }
}
