package me.fungames.jfortniteparse.ue4.assets.exports;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.enums.ETickingGroup;
import me.fungames.jfortniteparse.ue4.assets.enums.ETimelineLengthMode;
import me.fungames.jfortniteparse.ue4.assets.objects.FBPVariableMetaDataEntry;
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid;
import me.fungames.jfortniteparse.ue4.objects.engine.curves.UCurveFloat;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class UTimelineTemplate extends UObject {
    public Float TimelineLength;
    public ETimelineLengthMode LengthMode;
    public Boolean bAutoPlay;
    public Boolean bLoop;
    public Boolean bReplicated;
    public Boolean bIgnoreTimeDilation;
    public List<FTTEventTrack> EventTracks;
    public List<FTTFloatTrack> FloatTracks;
    public List<FTTVectorTrack> VectorTracks;
    public List<FTTLinearColorTrack> LinearColorTracks;
    public List<FBPVariableMetaDataEntry> MetaDataArray;
    public FGuid TimelineGuid;
    public ETickingGroup TimelineTickGroup;
    public FName VariableName;
    public FName DirectionPropertyName;
    public FName UpdateFunctionName;
    public FName FinishedFunctionName;

    @UStruct
    public static class FTTTrackBase {
        public FName TrackName;
        public Boolean bIsExternalCurve;
    }

    @UStruct
    public static class FTTEventTrack extends FTTTrackBase {
        public FName FunctionName;
        public Lazy<UCurveFloat> CurveKeys;
    }

    @UStruct
    public static class FTTPropertyTrack extends FTTTrackBase {
        public FName PropertyName;
    }

    @UStruct
    public static class FTTFloatTrack extends FTTPropertyTrack {
        public Lazy<UCurveFloat> CurveFloat;
    }

    @UStruct
    public static class FTTVectorTrack extends FTTPropertyTrack {
        public FPackageIndex /*CurveVector*/ CurveVector;
    }

    @UStruct
    public static class FTTLinearColorTrack extends FTTPropertyTrack {
        public FPackageIndex /*CurveLinearColor*/ CurveLinearColor;
    }
}
