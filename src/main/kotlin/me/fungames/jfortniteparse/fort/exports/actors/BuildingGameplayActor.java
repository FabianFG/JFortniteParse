package me.fungames.jfortniteparse.fort.exports.actors;

import kotlin.Lazy;
import me.fungames.jfortniteparse.fort.objects.FortAbilitySetDeliveryInfo;
import me.fungames.jfortniteparse.fort.objects.FortDeliveryInfoRequirementsFilter;
import me.fungames.jfortniteparse.fort.objects.GameplayEffectApplicationInfoHard;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.actors.AActor;
import me.fungames.jfortniteparse.ue4.assets.exports.components.UPrimitiveComponent;
import me.fungames.jfortniteparse.ue4.objects.FDataTableRowHandle;
import me.fungames.jfortniteparse.ue4.objects.FScalableFloat;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FMulticastScriptDelegate;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class BuildingGameplayActor extends BuildingActor {
    public FPackageIndex /*FortAbilitySet*/ AbilitySet;
    @UProperty(arrayDim = 5)
    public FPackageIndex /*FortAbilitySet*/ InherentAbilitySets;
    public FPackageIndex /*FortDamageSet*/ DamageSet;
    public FDataTableRowHandle DamageStatHandle;
    public Boolean bAllowRidingOnActor;
    public FMulticastScriptDelegate OnProximityPulseDelegate;
    public FMulticastScriptDelegate OnProximityPrePulseDelegate;
    public FMulticastScriptDelegate OnProximityEffectsAppliedDelegate;
    public FMulticastScriptDelegate OnDeliverableAbilityInfoAppliedToTouchingActor;
    public FMulticastScriptDelegate OnDeliverableAbilityInfoChangedForExitingTouchActor;
    public Boolean bIgnoreInstigatorCollision;
    public Boolean bAddOwnerVelocity;
    public Integer AbilitySourceLevel;
    public BuildingGameplayActorAbilityDeliveryInfo DeliverableAbilityInfo;
    public Boolean bApplyDefaultEnabledAbilityBucketsOnInit;
    public Boolean bUseSimpleActorTouchSetupForAbilityBuckets;
    public Boolean bCanBeMarked;
    public Boolean bBlockMarking;
    public MarkedActorDisplayInfo MarkerDisplay;
    public FVector MarkerPositionOffset;
    public Boolean bShowInteractKeybind;
    public Lazy<UPrimitiveComponent> RegisteredTouchComponent;
    public FGameplayTagContainer AnalyticsTags;
    public Float PostProcessOverlapBlendWeight;

    @UStruct
    public static class BuildingGameplayActorAbilityDeliveryInfo {
        public List<BuildingGameplayActorAbilityDeliveryBucket> DeliveryBuckets;
        public FScalableFloat ProximityPulseInterval;
        public FScalableFloat ProximityPrePulseTime;
        public Boolean bHasGEsToApplyOnTouch;
        public Boolean bHasGEsToApplyOnExit;
        public Boolean bHasGEsToApplyOnPulseTimer;
        public Boolean bHasPersistentEffects;
        public Lazy<BuildingGameplayActor> OwningActor;
        public List<Lazy<AActor>> DeferredTouchActorsToProcess;
    }

    @UStruct
    public static class BuildingGameplayActorAbilityDeliveryBucket {
        public FGameplayTag Tag;
        public List<ProximityBasedGEDeliveryInfoHard> ProximityEffectBuckets;
        public List<FortAbilitySetDeliveryInfo> PawnPersistentAbilitySetBuckets;
        public List<FortAbilitySetHandle> PersistentlyAppliedAbilitySets;
        public Boolean bEnabled;
        public Boolean bEnabledByDefault;
        public Boolean bHasGEsToApplyOnTouch;
        public Boolean bHasGEsToApplyOnExit;
        public Boolean bHasGEsToApplyOnPulseTimer;
        public Boolean bHasPersistentEffects;
    }

    @UStruct
    public static class ProximityBasedGEDeliveryInfoBase {
        public FortDeliveryInfoRequirementsFilter DeliveryRequirements;
        public EFortProximityBasedGEApplicationType ProximityApplicationType;
    }

    public enum EFortProximityBasedGEApplicationType {
        ApplyOnProximityPulse,
        ApplyOnProximityTouch,
        ApplyOnlyDuringProximityTouch,
        ApplyOnProximityExit,
        ApplyOnProximityPrePulse
    }

    @UStruct
    public static class ProximityBasedGEDeliveryInfoHard extends ProximityBasedGEDeliveryInfoBase {
        public List<GameplayEffectApplicationInfoHard> EffectsToApply;
    }

    @UStruct
    public static class FortAbilitySetHandle {
        public FPackageIndex /*AbilitySystemComponent*/ TargetAbilitySystemComponent;
        public List<FGameplayAbilitySpecHandle> GrantedAbilityHandles;
        public List<FActiveGameplayEffectHandle> AppliedEffectHandles;
        public List<FGuid> ItemGuidsForAdditionalItems;
    }

    @UStruct
    public static class FGameplayAbilitySpecHandle { // this and below belongs to GameplayAbilities
        public Integer Handle;
    }

    @UStruct
    public static class FActiveGameplayEffectHandle {
        public Integer Handle;
        public Boolean bPassedFiltersAndWasExecuted;
    }
}
