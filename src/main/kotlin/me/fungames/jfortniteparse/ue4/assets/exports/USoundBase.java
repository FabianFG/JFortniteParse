package me.fungames.jfortniteparse.ue4.assets.exports;

import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

public class USoundBase extends UObject {
    public FPackageIndex /*USoundClass*/ SoundClassObject;
    public boolean bDebug;
    public boolean bOverrideConcurrency;
    public boolean bOutputToBusOnly;
    public boolean bHasDelayNode;
    public boolean bHasConcatenatorNode;
    public boolean bBypassVolumeScaleForPriority;
    public EVirtualizationMode VirtualizationMode;
    public FSoundConcurrencySettings ConcurrencyOverrides;
    public float Duration;
    public float MaxDistance;
    public float TotalSamples;
    public float Priority;
    public FPackageIndex /*USoundAttenuation*/ AttenuationSettings;
    public FSoundModulationDestinationSettings VolumeModulationDestination;
    public FSoundModulationDestinationSettings PitchModulationDestination;
    public FSoundModulationDestinationSettings HighpassModulationDestination;
    public FSoundModulationDestinationSettings LowpassModulationDestination;
    public FPackageIndex /*USoundSubmixBase*/ SoundSubmixObject;
    //public List<FSoundSubmixSendInfo> SoundSubmixSends;
    @UProperty(skipPrevious = 1, skipNext = 2)
    public FPackageIndex /*USoundEffectSourcePresetChain*/ SourceEffectChain;
    //public List<FSoundSourceBusSendInfo> BusSends;
    //public List<FSoundSourceBusSendInfo> PreEffectBusSends;

    public enum EVirtualizationMode {
        Disabled,
        PlayWhenSilent,
        Restart
    }

    @UStruct
    public static class FSoundConcurrencySettings {
        public int MaxCount;
        public boolean bLimitToOwner;
        public EMaxConcurrentResolutionRule ResolutionRule;
        public float RetriggerTime;
        public float VolumeScale;
        public EConcurrencyVolumeScaleMode VolumeScaleMode;
        public float VolumeScaleAttackTime;
        public boolean bVolumeScaleCanRelease;
        public float VolumeScaleReleaseTime;
        public float VoiceStealReleaseTime;
    }

    public enum EMaxConcurrentResolutionRule {
        PreventNew,
        StopOldest,
        StopFarthestThenPreventNew,
        StopFarthestThenOldest,
        StopLowestPriority,
        StopQuietest,
        StopLowestPriorityThenPreventNew,
        Count
    }

    public enum EConcurrencyVolumeScaleMode {
        Default,
        Distance,
        Priority
    }

    @UStruct
    public static class FSoundModulationDestinationSettings {
        public float Value;
        public FPackageIndex /*USoundModulatorBase*/ Modulator;
    }
}
