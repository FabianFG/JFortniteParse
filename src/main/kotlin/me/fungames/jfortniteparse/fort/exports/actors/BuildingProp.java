package me.fungames.jfortniteparse.fort.exports.actors;

import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;

public class BuildingProp extends BuildingTimeOfDayLights {
    @UProperty(skipPrevious = 4)
    public FGameplayTagContainer AnalyticsTags;
    public Boolean bSuppressSimpleInteractionWidgetForTouch;
    public Boolean bKeepWhenUnderwater;
    public Boolean bDoNotBlockMarkerTraceWhenOverlappingPlayer;
}
