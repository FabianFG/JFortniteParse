package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.core.math.FIntPoint;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class AthenaEmojiItemDefinition extends AthenaDanceItemDefinition {
    public FGameplayTag EmojiGameplayCueTag;
    public FSoftObjectPath PreviewAnimation;
    public FSoftObjectPath SpriteSheet;
    public FIntPoint SheetDimensions;
    public Integer FrameIndex;
    public Integer FrameCount;
    public FPackageIndex /*UMaterialInterface*/ BaseMaterial;
    public FVector IconSize;
    public FLinearColor InitialColor;
    public FVector InitialLocation;
    public FVector InitialVelocity;
    public Float LifetimeIntroSeconds;
    public Float LifetimeMidSeconds;
    public Float LifetimeOutroSeconds;
    public FPackageIndex /*UMaterialInstance*/ GeneratedMaterial;
}
