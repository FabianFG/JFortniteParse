package me.fungames.jfortniteparse.fort.exports.actors;

import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;

public class BuildingProp extends BuildingTimeOfDayLights {
    public FGameplayTagContainer AnalyticsTags;
    public Boolean bSuppressSimpleInteractionWidgetForTouch;
    public Boolean bKeepWhenUnderwater;
    public Boolean bCanBeMarked;
    public Boolean bBlockMarking;
    public MarkedActorDisplayInfo MarkerDisplay;
    public FVector MarkerPositionOffset;
    public Boolean bDoNotBlockMarkerTraceWhenOverlappingPlayer;
}
