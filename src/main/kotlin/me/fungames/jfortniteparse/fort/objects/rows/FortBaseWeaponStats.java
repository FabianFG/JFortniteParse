package me.fungames.jfortniteparse.fort.objects.rows;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.exports.UCurveTable;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable;
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

public class FortBaseWeaponStats extends FTableRowBase {
    public int BaseLevel;
    public FName NamedWeightRow;
    public float DmgPB;
    public float DmgMid;
    public float DmgLong;
    public float DmgMaxRange;
    public float EnvDmgPB;
    public float EnvDmgMid;
    public float EnvDmgLong;
    public float EnvDmgMaxRange;
    public float ImpactDmgPB;
    public float ImpactDmgMid;
    public float ImpactDmgLong;
    public float ImpactDmgMaxRange;
    public boolean bForceControl;
    public float RngPB;
    public float RngMid;
    public float RngLong;
    public float RngMax;
    public Lazy<UCurveTable> DmgScaleTable;
    public FName DmgScaleTableRow;
    public float DmgScale;
    public Lazy<UCurveTable> EnvDmgScaleTable;
    public FName EnvDmgScaleTableRow;
    public float EnvDmgScale;
    public Lazy<UCurveTable> ImpactDmgScaleTable;
    public FName ImpactDmgScaleTableRow;
    public float ImpactDmgScale;
    public FName SurfaceRatioRowName;
    public float DamageZone_Light;
    public float DamageZone_Normal;
    public float DamageZone_Critical;
    public float DamageZone_Vulnerability;
    public float KnockbackMagnitude;
    public float MidRangeKnockbackMagnitude;
    public float LongRangeKnockbackMagnitude;
    public float KnockbackZAngle;
    public float StunTime;
    public float StunScale;
    public Lazy<UDataTable> Durability;
    public FName DurabilityRowName;
    public float DurabilityScale;
    public float DurabilityPerUse;
    public float FullChargeDurabilityPerUse;
    public float DiceCritChance;
    public float DiceCritDamageMultiplier;
    public float ReloadTime;
    public float ReloadScale;
    public EFortWeaponReloadType ReloadType;
    public boolean bAllowReloadInterrupt;
    public boolean bReloadInterruptIsImmediate;
    public int NumIndividualBulletsToReload;
    public int ClipSize;
    public float ClipScale;
    public int InitialClips;
    public int CartridgePerFire;
    public int AmmoCostPerFire;
    public int MaxAmmoCostPerFire;
    public float MinChargeTime;
    public float MaxChargeTime;
    public float ChargeDownTime;
    public boolean bAutoDischarge;
    public float MaxChargeTimeUntilDischarge;
    public float MinChargeDamageMultiplier;
    public float MaxChargeDamageMultiplier;
    public FPackageIndex /*CurveFloat*/ ChargeDamageMultiplierCurve;
    public float EquipAnimRate;
    public float QuickBarSlotCooldownDuration;

    public enum EFortWeaponReloadType {
        ReloadWholeClip,
        ReloadIndividualBullets,
        ReloadBasedOnAmmoCostPerFire,
        ReloadBasedOnCartridgePerFire
    }
}
