package me.fungames.jfortniteparse.ue4.assets.objects;

import kotlin.UByte;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.enums.ECollisionChannel;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

@UStruct
public class FBodyInstance extends FBodyInstanceCore {
    public ECollisionChannel ObjectType;
    public ECollisionEnabled CollisionEnabled;
    public ESleepFamily SleepFamily;
    public EDOFMode DOFMode;
    public Boolean bUseCCD;
    public Boolean bIgnoreAnalyticCollisions;
    public Boolean bNotifyRigidBodyCollision;
    public Boolean bLockTranslation;
    public Boolean bLockRotation;
    public Boolean bLockXTranslation;
    public Boolean bLockYTranslation;
    public Boolean bLockZTranslation;
    public Boolean bLockXRotation;
    public Boolean bLockYRotation;
    public Boolean bLockZRotation;
    public Boolean bOverrideMaxAngularVelocity;
    public Boolean bOverrideMaxDepenetrationVelocity;
    public Boolean bOverrideWalkableSlopeOnInstance;
    public Boolean bInterpolateWhenSubStepping;
    public FName CollisionProfileName;
    public UByte PositionSolverIterationCount;
    public UByte VelocitySolverIterationCount;
    public FCollisionResponse CollisionResponses;
    public Float MaxDepenetrationVelocity;
    public Float MassInKgOverride;
    public Float LinearDamping;
    public Float AngularDamping;
    public FVector CustomDOFPlaneNormal;
    public FVector COMNudge;
    public Float MassScale;
    public FVector InertiaTensorScale;
    public FWalkableSlopeOverride WalkableSlopeOverride;
    public FPackageIndex /*PhysicalMaterial*/ PhysMaterialOverride;
    public Float MaxAngularVelocity;
    public Float CustomSleepThresholdMultiplier;
    public Float StabilizationThresholdMultiplier;
    public Float PhysicsBlendWeight;

    public enum ECollisionEnabled {
        NoCollision,
        QueryOnly,
        PhysicsOnly,
        QueryAndPhysics
    }

    public enum ESleepFamily { // PhysicsCore
        Normal,
        Sensitive,
        Custom
    }

    public enum EDOFMode {
        Default,
        SixDOF,
        YZPlane,
        XZPlane,
        XYPlane,
        CustomPlane,
        None
    }

    @UStruct
    public static class FCollisionResponse {
        public FCollisionResponseContainer ResponseToChannels;
        public List<FResponseChannel> ResponseArray;
    }

    @UStruct
    public static class FCollisionResponseContainer {
        public ECollisionResponse WorldStatic;
        public ECollisionResponse WorldDynamic;
        public ECollisionResponse Pawn;
        public ECollisionResponse Visibility;
        public ECollisionResponse Camera;
        public ECollisionResponse PhysicsBody;
        public ECollisionResponse Vehicle;
        public ECollisionResponse Destructible;
        public ECollisionResponse EngineTraceChannel1;
        public ECollisionResponse EngineTraceChannel2;
        public ECollisionResponse EngineTraceChannel3;
        public ECollisionResponse EngineTraceChannel4;
        public ECollisionResponse EngineTraceChannel5;
        public ECollisionResponse EngineTraceChannel6;
        public ECollisionResponse GameTraceChannel1;
        public ECollisionResponse GameTraceChannel2;
        public ECollisionResponse GameTraceChannel3;
        public ECollisionResponse GameTraceChannel4;
        public ECollisionResponse GameTraceChannel5;
        public ECollisionResponse GameTraceChannel6;
        public ECollisionResponse GameTraceChannel7;
        public ECollisionResponse GameTraceChannel8;
        public ECollisionResponse GameTraceChannel9;
        public ECollisionResponse GameTraceChannel10;
        public ECollisionResponse GameTraceChannel11;
        public ECollisionResponse GameTraceChannel12;
        public ECollisionResponse GameTraceChannel13;
        public ECollisionResponse GameTraceChannel14;
        public ECollisionResponse GameTraceChannel15;
        public ECollisionResponse GameTraceChannel16;
        public ECollisionResponse GameTraceChannel17;
        public ECollisionResponse GameTraceChannel18;
    }

    public enum ECollisionResponse {
        ECR_Ignore,
        ECR_Overlap,
        ECR_Block
    }

    @UStruct
    public static class FResponseChannel {
        public FName Channel;
        public ECollisionResponse Response;
    }

    @UStruct
    public static class FWalkableSlopeOverride {
        public EWalkableSlopeBehavior WalkableSlopeBehavior;
        public Float WalkableSlopeAngle;
    }

    public enum EWalkableSlopeBehavior {
        WalkableSlope_Default,
        WalkableSlope_Increase,
        WalkableSlope_Decrease,
        WalkableSlope_Unwalkable
    }
}
