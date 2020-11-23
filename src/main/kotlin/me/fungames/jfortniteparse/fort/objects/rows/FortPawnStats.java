package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

public class FortPawnStats extends FTableRowBase {
    public float MaximumHealth;
    public float SpeedWalk;
    public float SpeedRun;
    public float SpeedSprint;
    public float SpeedFly;
    public float SpeedCrouchedRun;
    public float SpeedCrouchedSprint;
    public float SpeedBackwardsMultiplier;
    public float SpeedDBNO;
    public float AccelerationStrafeMultiplierSprint;
    public float MinAnalogWalkSpeed;
    public float GroundFriction;
    public float BrakingDecelerationWalking;
    public float BrakingDecelerationFalling;
    public float BrakingDecelerationFlying;
    public float BrakingFrictionFactor;
    public float MaxAcceleration;
    public float MaxAccelerationFlying;
    public float JumpZVelocity;
    public FPackageIndex /*CurveTable*/ FallingDamageTable;
    public FName FallingDamageTableRow;
    public FPackageIndex /*CurveTable*/ VehicleEjectDamageTable;
    public float HealthRegenRate;
    public float HealthRegenDelay;
    public float HealthRegenThreshold;
    public float MaxShield;
    public float ShieldRegenRate;
    public float ShieldRegenDelay;
    public float ShieldRegenThreshold;
    public float MaxControlResistance;
    public float ControlResistanceRegenRate;
    public float ControlResistanceRegenDelay;
    public float ControlResistanceRegenThreshold;
    public float KnockbackMultiplier;
    public float KnockbackThreshold;
    public boolean bAllowChainStun;
    public EFortControlRecoveryBehavior ControlRecoveryBehavior;
    public FGameplayTag CurieMaterialIdentifier;

    public enum EFortControlRecoveryBehavior {
        DefaultControl,
        LimitedControl,
        ChainControl
    }
}
