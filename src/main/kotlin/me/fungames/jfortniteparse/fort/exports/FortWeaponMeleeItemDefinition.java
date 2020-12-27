package me.fungames.jfortniteparse.fort.exports;

import kotlin.Lazy;
import me.fungames.jfortniteparse.fort.enums.EPhysicalSurface;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.enums.EDetailMode;
import me.fungames.jfortniteparse.ue4.objects.core.math.FTransform;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;
import java.util.Map;

public class FortWeaponMeleeItemDefinition extends FortWeaponItemDefinition {
    public Lazy<MarshalledVFX_AuthoredDataConfig> ManagedVFX_Defaults;
    public FSoftObjectPath IdleEffect;
    public FSoftObjectPath IdleEffectNiagara;
    public FName IdleFXSocketName;
    public FSoftObjectPath SwingEffect;
    public FSoftObjectPath SwingEffectNiagara;
    public FName SwingFXSocketName;
    public FSoftObjectPath AnimTrails;
    public FSoftObjectPath AnimTrailsNiagara;
    public FName NiagaraSkeletonDIVariableName;
    public FName AnimTrailsFirstSocketName;
    public FName AnimTrailsSecondSocketName;
    public Float AnimTrailsWidth;
    public Boolean bUseAnimTrails;
    public Boolean bAttachAnimTrailsToWeapon;
    public Map<EPhysicalSurface, FSoftObjectPath> ImpactPhysicalSurfaceEffectsMap;
    public List<FSoftObjectPath> ImpactNiagaraPhysicalSurfaceEffects;
    public Map<EPhysicalSurface, FSoftObjectPath> ImpactNiagaraPhysicalSurfaceEffectsMap;
    public List<AttachedParticleComponentDef> ParticleComponentsDefs;
    public List<FSoftObjectPath> WeaponMaterialOverrides;
    public FSoftObjectPath /*SoftClassPath*/ AnimClass;
    public FSoftObjectPath SingleAnimationToPlay;
    public Map<EPhysicalSurface, FSoftObjectPath> ImpactPhysicalSurfaceSoundsMap;
    public Map<EFortReloadFXState, FSoftObjectPath> ReloadSoundsMap;
    public Map<EFortWeaponSoundState, FSoftObjectPath> PrimaryFireSoundMap;
    public FSoftObjectPath GenericImpactSound;
    public Boolean bNeedsMaterial0MID;
    public Boolean bWatchKills;
    public Boolean bCandyCaneKillReaction;

    @UStruct
    public static class AttachedParticleComponentDef {
        public FTransform Transform;
        public FName ParentSocket;
        public FSoftObjectPath Template;
        public EDetailMode DetailMode;
    }

    public enum EFortReloadFXState {
        ReloadStart,
        ReloadCartridge,
        ReloadEnd,
        Max_None
    }

    public enum EFortWeaponSoundState {
        Normal,
        LowAmmo,
        Degraded,
        Max_None
    }
}
