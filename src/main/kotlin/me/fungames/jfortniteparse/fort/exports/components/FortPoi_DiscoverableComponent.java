package me.fungames.jfortniteparse.fort.exports.components;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.components.UActorComponent;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortPoi_DiscoverableComponent extends UActorComponent {
    public FVector MapLocationTextLocationOffset;
    public FText PlayerFacingName;
    public Boolean bDisableMapLocationText;
    public FSoftObjectPath QuestPartOf;
    public FName QuestObjectiveBackendName;
    public Integer DiscoverMinimapBitId;
    public List<EventDrivenDiscoveryID> EventDrivenBitIds;

    @UStruct
    public static class EventDrivenDiscoveryID {
        public String CalendarEventName;
        public Boolean bRequireEventActive;
        public Integer ActiveBitId;
    }
}
