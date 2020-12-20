package me.fungames.jfortniteparse.fort.exports.actors;

import me.fungames.jfortniteparse.ue4.assets.UStruct;

public class BuildingPropWall extends BuildingProp {
    public BuildingActorNavArea AreaPatternOverride;
    public EBuildingWallArea AreaShapeType;
    public Float AreaWidthOverride;
    public Boolean bOverrideAreaWidth;

    @UStruct
    public static class BuildingActorNavArea {
        public Integer AreaBits;
    }

    public enum EBuildingWallArea {
        Regular,
        Flat,
        Special
    }
}
