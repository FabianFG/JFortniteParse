package me.fungames.jfortniteparse.fort.exports.actors;

import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class BuildingTimeOfDayLights extends BuildingAutoNav {
    public Boolean bUseTimeOfDayControlledLights;
    public List<FPackageIndex /*LightComponent*/> TimeOfDayControlledLights;
    public List<FVector> TimeOfDayControlledLightsPositions;
    public List<Float> TimeOfDayControlledLightsInitalIntensities;
}
