package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.FDataTableRowHandle;
import me.fungames.jfortniteparse.ue4.objects.FScalableFloat;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector2D;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortWeaponItemDefinition extends FortWorldItemDefinition {
    public FSoftObjectPath /*SoftClassPath*/ WeaponActorClass;
    public FSoftObjectPath WeaponMeshOverride;
    public FSoftObjectPath IntrinsicOverrideWrap;
    public FDataTableRowHandle WeaponStatHandle;
    public FScalableFloat WeaponRechargeAmmoRate;
    public FScalableFloat WeaponRechargeAmmoQuantity;
    public FSoftObjectPath AbilitySet;
    public FName AlterationSlotsLoadoutRow;
    public FName BaselineAlterationSlotsLoadoutRow;
    public FSoftObjectPath BaseAlteration;
    public FSoftObjectPath BaseCosmeticAlteration;
    @UProperty(skipPrevious = 1) // TODO a missing property preventing parsing of 5 star Sunbeam Nocturno
    public FSoftObjectPath /*SoftClassPath*/ PrimaryFireAbility;
    public FSoftObjectPath /*SoftClassPath*/ SecondaryFireAbility;
    public FSoftObjectPath /*SoftClassPath*/ ReloadAbility;
    public FSoftObjectPath /*SoftClassPath*/ OnHitAbility;
    public List<FSoftObjectPath /*SoftClassPath*/> EquippedAbilities;
    public FSoftObjectPath EquippedAbilitySet;
    public List<FPackageIndex /*CustomCharacterPart*/> EquippedCharacterParts;
    public FSoftObjectPath AmmoData;
    public FPackageIndex /*FortWeaponAdditionalData*/ AdditionalData;
    public Float LowAmmoPercentage;
    public EFortWeaponTriggerType TriggerType;
    public EFortWeaponTriggerType SecondaryTriggerType;
    public EFortDisplayTier DisplayTier;
    public Boolean bUsesPhantomReserveAmmo;
    public Boolean bUsesCustomAmmoType;
    public Boolean bAllowSecondaryFireToInterruptPrimary;
    public Boolean bAllowTargetingDuringReload;
    public Boolean bTargetingPreventsReload;
    public Boolean bCanFireWhileInstigatorTethered;
    public Boolean bCanFireWhileNotTargetedInVehicle;
    public Boolean bAlwaysChargeUpToMin;
    public Boolean bReticleCornerOutsideSpreadRadius;
    public Boolean bValidForLastEquipped;
    public Boolean bPreventDefaultPreload;
    public Boolean bRequestClientPreload;
    public Float HitNotifyDuration;
    public FSoftObjectPath ReticleImage;
    public List<Float> ReticleCornerAngles;
    public FSoftObjectPath ReticleCenterImage;
    public FSoftObjectPath ReticleCenterPerfectAimImage;
    public FVector2D ReticleCenterImageOffset;
    public FSoftObjectPath ReticleInvalidTargetImage;
    public FGameplayTagContainer AnalyticTags;
    public FGameplayTagContainer PlayerGrantedGameplayTags;
    public List<FName> ActualAnalyticFNames;
    public FortCreativeTagsHelper CreativeTagsHelper;

    public enum EFortWeaponTriggerType {
        OnPress,
        Automatic,
        OnRelease,
        OnPressAndRelease
    }

    public enum EFortDisplayTier {
        Invalid,
        Handmade,
        Copper,
        Silver,
        Malachite,
        Obsidian,
        Brightcore,
        Spectrolite,
        Shadowshard,
        Sunbeam,
        Moonglow
    }

    @UStruct
    public static class FortCreativeTagsHelper {
        public List<FName> CreativeTags;
    }
}
