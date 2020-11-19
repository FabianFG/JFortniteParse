package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortWeaponRangedItemDefinition extends FortWeaponItemDefinition {
    public FSoftObjectPath /*SoftClassPath*/ ProjectileTemplate;
    public FSoftObjectPath BulletShellFXTemplate;
    public Boolean bUseNativeWeaponTrace;
    public Boolean bTraceThroughPawns;
    public Boolean bTraceThroughWorld;
    public Boolean bShouldSpawnBulletShellFX;
    public Boolean bShouldUsePerfectAimWhenTargetingMinSpread;
    public Boolean bDoNotAllowDoublePump;
    public Boolean bUseOnTouch;
    public Boolean bAllowADSInAir;
    public Boolean bShowReticleHitNotifyAtImpactLocation;
    public Boolean bForceProjectileTooltip;
    public Boolean bSecondaryFireRequiresAmmo;
}
