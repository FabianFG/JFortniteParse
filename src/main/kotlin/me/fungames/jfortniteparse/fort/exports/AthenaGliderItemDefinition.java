package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.MarshalledVFXAuthoredData;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.Map;

public class AthenaGliderItemDefinition extends AthenaCosmeticItemDefinition {
    public EFortGliderType GliderType;
    public FVector CameraFramingBoundsCenterOffset;
    public Boolean bActivateTrailsOnRotationalMovement;
    public FSoftObjectPath /*SoftClassProperty*/ ParachutePrefabClass;
    public FSoftObjectPath SkeletalMesh;
    public FSoftObjectPath /*SoftClassProperty*/ AnimClass;
    public FSoftObjectPath /*SoftClassProperty*/ CameraClass;
    public FSoftObjectPath PlayerAnimSet;
    public FSoftObjectPath TrailParticles;
    public FSoftObjectPath OwnerTrailParticles;
    public FName TrailParamName;
    public FName AttachSocket;
    public FSoftObjectPath TrailEffect;
    public FSoftObjectPath TrailEffectNiagara;
    public FSoftObjectPath TrailEffect2;
    public FSoftObjectPath TrailEffectNiagara2;
    public Boolean bAutoActivate;
    public FName DeployEffectTagName;
    public Boolean CleanUpDeployEffect;
    public Boolean bAttachNiagaraEffectToPlayerPawn;
    public FName UserSkeletonParameterName;
    public FSoftObjectPath OpenSound;
    public FSoftObjectPath CloseSound;
    public FSoftObjectPath ThrustLoopSound;
    public Map<ELayeredAudioTriggerDirection, FSoftObjectPath> ThrustStartSounds;
    public MarshalledVFX_AuthoredDataConfig AuthoredData;
    @Deprecated
    public MarshalledVFXAuthoredData AuthoredParticleData;

    public enum EFortGliderType {
        Glider,
        Umbrella
    }

    public enum ELayeredAudioTriggerDirection {
        AnyDirection,
        Forwards,
        Sideways,
        Backwards
    }
}
