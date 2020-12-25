package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.FortUICameraFrameTargetBounds;
import me.fungames.jfortniteparse.ue4.objects.core.math.FRotator;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortWeaponAdditionalData_SingleWieldState extends FortWeaponAdditionalData {
    public FGameplayTag AssociatedTagVariant;
    public FSoftObjectPath /*SoftClassPath*/ PrimaryFireAbility_InState;
    public FSoftObjectPath AnimSet_InState;
    public FSoftObjectPath EquipMontage_InState;
    public Boolean bHideOffhandMesh;
    public Boolean bUseSeparatePreviewOffsets;
    public FVector ImpactFxPreviewOffset;
    public FVector OffHandPreviewOffset;
    public FRotator OffHandPreviewRotation;
    public FRotator InitialPreviewRotation;
    public Float FrontendPreviewScale;
    public FortUICameraFrameTargetBounds CameraFramingBounds;
    public EFortWeaponCoreAnimation AnimationStyleToUse;
    public FPackageIndex /*FortGameplayAbility*/ LiveAbility;
    public FPackageIndex /*FortWeaponAnimSet*/ LiveAnimSet;
    public FPackageIndex /*AnimMontage*/ LiveMontage;

    public enum EFortWeaponCoreAnimation {
        Melee,
        Pistol,
        Shotgun,
        PaperBlueprint,
        Rifle,
        MeleeOneHand,
        MachinePistol,
        RocketLauncher,
        GrenadeLauncher,
        GoingCommando,
        AssaultRifle,
        TacticalShotgun,
        SniperRifle,
        TrapPlacement,
        ShoulderLauncher,
        AbilityDecoTool,
        Crossbow,
        C4,
        RemoteControl,
        DualWield,
        AR_BullPup,
        AR_ForwardGrip,
        MedPackPaddles,
        SMG_P90,
        AR_DrumGun,
        Consumable_Small,
        Consumable_Large,
        Balloon,
        MountedTurret,
        CreativeTool,
        ExplosiveBow,
        AshtonIndigo,
        AshtonChicago,
        MeleeDualWield
    }
}
