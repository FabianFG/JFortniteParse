package me.fungames.jfortniteparse.fort.exports;

import kotlin.Lazy;
import me.fungames.jfortniteparse.fort.objects.FortUICameraFrameTargetBounds;
import me.fungames.jfortniteparse.ue4.objects.core.math.FRotator;
import me.fungames.jfortniteparse.ue4.objects.core.math.FTransform;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class AthenaPickaxeItemDefinition extends AthenaCosmeticItemDefinition {
    public Lazy<FortWeaponMeleeItemDefinition> WeaponDefinition;
    public FName MainMeshAttachmentSocketName;
    public FName OffhandMeshAttachmentSocketName;
    public FTransform MainMeshRelativeTransform;
    public FTransform OffhandMeshRelativeTransform;
    public FVector PickaxeImpactFXPreviewOffset;
    public FVector PickaxeDualWieldPreviewOffset;
    public FRotator PickaxeDualWieldPreviewRotation;
    public FSoftObjectPath PreviewIdleMontage;
    public FSoftObjectPath PreviewSwingMontage;
    public FortUICameraFrameTargetBounds CameraFramingBounds;
    public FVector CameraFramingBoundsCenterOffset;
}
