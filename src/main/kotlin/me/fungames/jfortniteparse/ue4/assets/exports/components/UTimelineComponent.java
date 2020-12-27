package me.fungames.jfortniteparse.ue4.assets.exports.components;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.enums.ETimelineLengthMode;
import me.fungames.jfortniteparse.ue4.assets.exports.UObject;
import me.fungames.jfortniteparse.ue4.objects.engine.curves.UCurveFloat;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FScriptDelegate;

import java.util.List;

public class UTimelineComponent extends UActorComponent {
    public FTimeline TheTimeline;
    public Boolean bIgnoreTimeDilation;

    @UStruct
    public static class FTimeline {
        public ETimelineLengthMode LengthMode;
        public Boolean bLooping;
        public Boolean bReversePlayback;
        public Boolean bPlaying;
        public Float Length;
        public Float PlayRate;
        public Float position;
        public List<FTimelineEventEntry> Events;
        public List<FTimelineVectorTrack> InterpVectors;
        public List<FTimelineFloatTrack> InterpFloats;
        public List<FTimelineLinearColorTrack> InterpLinearColors;
        public FScriptDelegate TimelinePostUpdateFunc;
        public FScriptDelegate TimelineFinishedFunc;
        public Lazy<UObject> /*WeakObjectProperty*/ PropertySetObject;
        public FName DirectionPropertyName;
    }

    @UStruct
    public static class FTimelineEventEntry {
        public Float Time;
        public FScriptDelegate EventFunc;
    }

    @UStruct
    public static class FTimelineVectorTrack {
        public FPackageIndex /*CurveVector*/ VectorCurve;
        public FScriptDelegate InterpFunc;
        public FName TrackName;
        public FName VectorPropertyName;
    }

    @UStruct
    public static class FTimelineFloatTrack {
        public Lazy<UCurveFloat> FloatCurve;
        public FScriptDelegate InterpFunc;
        public FName TrackName;
        public FName FloatPropertyName;
    }

    @UStruct
    public static class FTimelineLinearColorTrack {
        public FPackageIndex /*CurveLinearColor*/ LinearColorCurve;
        public FScriptDelegate InterpFunc;
        public FName TrackName;
        public FName LinearColorPropertyName;
    }
}
