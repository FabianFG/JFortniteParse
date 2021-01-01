package me.fungames.jfortniteparse.ue4.assets.exports.components;

import kotlin.Lazy;
import me.fungames.jfortniteparse.fort.enums.EAttachmentRule;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.enums.ECollisionChannel;
import me.fungames.jfortniteparse.ue4.assets.exports.USoundBase;
import me.fungames.jfortniteparse.ue4.assets.exports.USoundWave;
import me.fungames.jfortniteparse.ue4.objects.engine.curves.FRuntimeFloatCurve;
import me.fungames.jfortniteparse.ue4.objects.uobject.FMulticastScriptDelegate;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FScriptDelegate;

import java.util.List;
import java.util.Set;

public class UAudioComponent extends USceneComponent {
    public Lazy<USoundBase> Sound;
    public List<FAudioComponentParam> InstanceParameters;
    public FPackageIndex /*SoundClass*/ SoundClassOverride;
    public Boolean bAutoDestroy;
    public Boolean bStopWhenOwnerDestroyed;
    public Boolean bShouldRemainActiveIfDropped;
    public Boolean bAllowSpatialization;
    public Boolean bOverrideAttenuation;
    public Boolean bOverrideSubtitlePriority;
    public Boolean bIsUISound;
    public Boolean bEnableLowPassFilter;
    public Boolean bOverridePriority;
    public Boolean bSuppressSubtitles;
    public Boolean bAutoManageAttachment;
    public FName AudioComponentUserID;
    public Float PitchModulationMin;
    public Float PitchModulationMax;
    public Float VolumeModulationMin;
    public Float VolumeModulationMax;
    public Float VolumeMultiplier;
    public Integer EnvelopeFollowerAttackTime;
    public Integer EnvelopeFollowerReleaseTime;
    public Float Priority;
    public Float SubtitlePriority;
    public FPackageIndex /*SoundEffectSourcePresetChain*/ SourceEffectChain;
    public Float PitchMultiplier;
    public Float LowPassFilterFrequency;
    public FPackageIndex /*SoundAttenuation*/ AttenuationSettings;
    public FSoundAttenuationSettings AttenuationOverrides;
    public FPackageIndex /*SoundConcurrency*/ ConcurrencySettings;
    public Set<FPackageIndex /*SoundConcurrency*/> ConcurrencySet;
    public EAttachmentRule AutoAttachLocationRule;
    public EAttachmentRule AutoAttachRotationRule;
    public EAttachmentRule AutoAttachScaleRule;
    public FMulticastScriptDelegate OnAudioPlayStateChanged;
    public FMulticastScriptDelegate OnAudioVirtualizationChanged;
    public FMulticastScriptDelegate OnAudioFinished;
    public FMulticastScriptDelegate OnAudioPlaybackPercent;
    public FMulticastScriptDelegate OnAudioSingleEnvelopeValue;
    public FMulticastScriptDelegate OnAudioMultiEnvelopeValue;
    public FScriptDelegate OnQueueSubtitles;
    public Lazy<USceneComponent> /*WeakObjectProperty*/ AutoAttachParent;
    public FName AutoAttachSocketName;

    @UStruct
    public static class FAudioComponentParam {
        public FName ParamName;
        public Float FloatParam;
        public Boolean BoolParam;
        public Integer IntParam;
        public Lazy<USoundWave> SoundWaveParam;
    }

    @UStruct
    public static class FSoundAttenuationSettings {
        public Boolean bAttenuate;
        public Boolean bSpatialize;
        public Boolean bAttenuateWithLPF;
        public Boolean bEnableListenerFocus;
        public Boolean bEnableFocusInterpolation;
        public Boolean bEnableOcclusion;
        public Boolean bUseComplexCollisionForOcclusion;
        public Boolean bEnableReverbSend;
        public Boolean bEnablePriorityAttenuation;
        public Boolean bApplyNormalizationToStereoSounds;
        public Boolean bEnableLogFrequencyScaling;
        public Boolean bEnableSubmixSends;
        public ESoundSpatializationAlgorithm SpatializationAlgorithm;
        public Float BinauralRadius;
        public EAirAbsorptionMethod AbsorptionMethod;
        public ECollisionChannel OcclusionTraceChannel;
        public EReverbSendMethod ReverbSendMethod;
        public EPriorityAttenuationMethod PriorityAttenuationMethod;
        public Float OmniRadius;
        public Float StereoSpread;
        public Float LPFRadiusMin;
        public Float LPFRadiusMax;
        public FRuntimeFloatCurve CustomLowpassAirAbsorptionCurve;
        public FRuntimeFloatCurve CustomHighpassAirAbsorptionCurve;
        public Float LPFFrequencyAtMin;
        public Float LPFFrequencyAtMax;
        public Float HPFFrequencyAtMin;
        public Float HPFFrequencyAtMax;
        public Float FocusAzimuth;
        public Float NonFocusAzimuth;
        public Float FocusDistanceScale;
        public Float NonFocusDistanceScale;
        public Float FocusPriorityScale;
        public Float NonFocusPriorityScale;
        public Float FocusVolumeAttenuation;
        public Float NonFocusVolumeAttenuation;
        public Float FocusAttackInterpSpeed;
        public Float FocusReleaseInterpSpeed;
        public Float OcclusionLowPassFilterFrequency;
        public Float OcclusionVolumeAttenuation;
        public Float OcclusionInterpolationTime;
        public Float ReverbWetLevelMin;
        public Float ReverbWetLevelMax;
        public Float ReverbDistanceMin;
        public Float ReverbDistanceMax;
        public Float ManualReverbSendLevel;
        public FRuntimeFloatCurve CustomReverbSendCurve;
        public List<FAttenuationSubmixSendSettings> SubmixSendSettings;
        public Float PriorityAttenuationMin;
        public Float PriorityAttenuationMax;
        public Float PriorityAttenuationDistanceMin;
        public Float PriorityAttenuationDistanceMax;
        public Float ManualPriorityAttenuation;
        public FRuntimeFloatCurve CustomPriorityAttenuationCurve;
        public FSoundAttenuationPluginSettings PluginSettings;
    }

    public enum ESoundSpatializationAlgorithm {
        SPATIALIZATION_Default,
        SPATIALIZATION_HRTF
    }

    public enum EAirAbsorptionMethod {
        Linear,
        CustomCurve
    }

    public enum EReverbSendMethod {
        Linear,
        CustomCurve,
        Manual
    }

    public enum EPriorityAttenuationMethod {
        Linear,
        CustomCurve,
        Manual
    }

    @UStruct
    public static class FAttenuationSubmixSendSettings {
        public FPackageIndex /*SoundSubmixBase*/ Submix;
        public ESubmixSendMethod SubmixSendMethod;
        public Float SubmixSendLevelMin;
        public Float SubmixSendLevelMax;
        public Float SubmixSendDistanceMin;
        public Float SubmixSendDistanceMax;
        public Float ManualSubmixSendLevel;
        public FRuntimeFloatCurve CustomSubmixSendCurve;
    }

    public enum ESubmixSendMethod {
        Linear,
        CustomCurve,
        Manual
    }

    @UStruct
    public static class FSoundAttenuationPluginSettings {
        List<FPackageIndex /*SpatializationPluginSourceSettingsBase*/> SpatializationPluginSettingsArray;
        List<FPackageIndex /*OcclusionPluginSourceSettingsBase*/> OcclusionPluginSettingsArray;
        List<FPackageIndex /*ReverbPluginSourceSettingsBase*/> ReverbPluginSettingsArray;
    }
}
