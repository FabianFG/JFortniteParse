package me.fungames.jfortniteparse.fort.exports;

import kotlin.UByte;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.Map;

public class FortWeaponMeleeDualWieldItemDefinition extends FortWeaponMeleeItemDefinition {
    public FSoftObjectPath WeaponMeshOffhandOverride;
    public FSoftObjectPath IdleEffectOffhand;
    public FSoftObjectPath IdleEffectOffhandNiagara;
    public FSoftObjectPath SwingEffectOffhand;
    public FSoftObjectPath SwingEffectOffhandNiagara;
    public FSoftObjectPath AnimTrailsOffhand;
    public FSoftObjectPath AnimTrailsNiagaraOffhand;
    public FSoftObjectPath OffhandGenericImpactSound;
    public Map<UByte, FSoftObjectPath> OffhandImpactPhysicalSurfaceSoundsMap;
    public Map<UByte, FSoftObjectPath> OffhandImpactPhysicalSurfaceEffects;
    public Map<UByte, FSoftObjectPath> OffhandImpactNiagaraPhysicalSurfaceEffects;
    public Map<UByte, FSoftObjectPath> OffhandPrimaryFireSoundMap;
    public FPackageIndex /*MarshalledVFX_AuthoredDataConfig*/ ManagedVFX_OffhandDefaults;
    public FName AnimTrailsOffhandFirstSocketName;
    public FName AnimTrailsOffhandSecondSocketName;
    public Float AnimTrailsOffhandWidth;
    public Boolean bUseAnimTrailsOffhand;
    public Boolean bAttachAnimTrailsOffhandToWeapon;
    public FName IdleFXOffhandSocketName;
    public FName SwingFXOffhandSocketName;
}
